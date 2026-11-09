package org.java_proj.gym_management_system.features.users.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.java_proj.gym_management_system.common.constant.Status;
import org.java_proj.gym_management_system.common.storage.StorageService;
import org.java_proj.gym_management_system.common.storage.StorageServiceFactory;
import org.java_proj.gym_management_system.common.util.ServerUtil;
import org.java_proj.gym_management_system.config.exceptions.EntityCreationException;
import org.java_proj.gym_management_system.config.exceptions.UnauthorizedException;
import org.java_proj.gym_management_system.config.response.dto.ApiResponse;
import org.java_proj.gym_management_system.features.profile.dto.request.ProfileProjection;
import org.java_proj.gym_management_system.features.userDetailInfo.dto.response.UserDetailInfoResponseDto;
import org.java_proj.gym_management_system.features.userDetailInfo.repository.UserDetailInfoRepository;
import org.java_proj.gym_management_system.features.profile.dto.response.ProfileResponseDto;
import org.java_proj.gym_management_system.features.users.dto.request.AuthRequestDto;
import org.java_proj.gym_management_system.features.users.dto.request.UserCreateRequest;
import org.java_proj.gym_management_system.features.users.dto.request.UserLoginProjection;
import org.java_proj.gym_management_system.features.users.dto.request.UserTokenProjection;
import org.java_proj.gym_management_system.features.users.dto.response.LoginResponseDto;
import org.java_proj.gym_management_system.features.users.dto.response.UserResponseDto;
import org.java_proj.gym_management_system.features.users.repository.ProfileRepository;
import org.java_proj.gym_management_system.features.users.repository.RoleRepository;
import org.java_proj.gym_management_system.features.users.repository.UserRepository;
import org.java_proj.gym_management_system.features.users.repository.UserTokenRepository;
import org.java_proj.gym_management_system.features.users.service.UserService;
import org.java_proj.gym_management_system.model.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final RoleRepository roleRepository;
    private final org.java_proj.gym_management_system.common.util.OtpGuard otpGuard;
    private final UserDetailInfoRepository userDetailInfoRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    private final ServerUtil serverUtil;
    private final UserTokenRepository userTokenRepository;
    private final StringRedisTemplate redisTemplate;

    private StorageService storageService;

    @Autowired
    public void setStorageService(StorageServiceFactory factory) {
        this.storageService = factory.getConfiguredStorageService();
    }

    @Transactional
    public ApiResponse createUser(UserCreateRequest request) {
        // Sign-up is public, so only member and trainer accounts may be created here.
        // (Trainers still need admin approval before they can log in.)
        if (!"MEMBER".equalsIgnoreCase(request.getRole()) && !"TRAINER".equalsIgnoreCase(request.getRole())) {
            throw new IllegalArgumentException("You can only register as a member or a trainer.");
        }
        final Role role = roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new EntityNotFoundException("Role not found."));

        // An account whose profile setup was never finished (the person verified the e-mail and left)
        // is not "taken": signing up again with a fresh code resumes it instead of failing forever.
        final User existing = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (existing != null && isSetupComplete(existing)) {
            throw new EntityCreationException("Email already exists.");
        }
        // Only addresses proven by the emailed code may register.
        if (!otpGuard.isEmailVerified(request.getEmail())) {
            throw new IllegalArgumentException("Please verify your email with the code we sent before registering.");
        }

        User user = existing != null ? existing : new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setStatus(Objects.equals(role.getName(), "TRAINER") ? Status.PENDING : Status.ACTIVE);

        userRepository.save(user);
        otpGuard.clearEmailVerified(request.getEmail());

        UserResponseDto dto = modelMapper.map(user, UserResponseDto.class);
        dto.setRoleName(role.getName());
        return ApiResponse.builder().success(1).code(HttpStatus.CREATED.value())
                .data(Map.of("currentUser", dto))
                .message("User account created Successfully.").build();
    }

    @Transactional
    public String uploadProfilePicture(final Long userId, final MultipartFile file) {
        final Profile profile = this.profileRepository.findByUser_Id(userId)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found for user ID: " + userId));

        String fileUrl = storageService.storeAndGetUrl(file);

        profile.setProfilePic(fileUrl);
        this.profileRepository.save(profile);

        return fileUrl;
    }

    /** Profile and details both exist (admins have neither and are always complete). */
    private boolean hasFinishedSetup(Long userId) {
        return profileRepository.findByUser_Id(userId).isPresent()
                && userDetailInfoRepository.existsByUserIdAndStatus(userId, Status.ACTIVE);
    }

    private boolean isSetupComplete(User user) {
        return user.getRole() != null && "ADMIN".equals(user.getRole().getName()) || hasFinishedSetup(user.getId());
    }

    @Override
    public ApiResponse verifyEmail(String email) {
        final boolean taken = userRepository.findByEmail(email).map(this::isSetupComplete).orElse(false);
        if (taken) {
            return new ApiResponse(0, 409, null, new HashMap<>(), "Email already exists.");
        }
        otpGuard.checkResendCooldown("verify-account:", email);
        try {
            serverUtil.sendCodeToEmail(email, 15, "verifyAccountMail", "verify-account:");
            return new ApiResponse(1, 200, null, new HashMap<>(), "Verify Email Sent successfully.");
        } catch (Exception e) {
            return new ApiResponse(0, 400, null, new HashMap<>(), "Error Sending Mail. " + e.getMessage());
        }
    }

    @Override
    public ApiResponse resendCode(String email) {
        otpGuard.checkResendCooldown("verify-account:", email);
        try {
            serverUtil.sendCodeToEmail(email, 15, "verifyAccountMail", "verify-account:");
            return new ApiResponse(1, 200, null, new HashMap<>(), "Resend Mail Sent successfully.");
        } catch (Exception e) {
            return new ApiResponse(0, 400, null, new HashMap<>(), "Error Sending Mail");
        }
    }

    @Override
    public ApiResponse verifyAccount(long code, String email) {
        // Wrong guesses are counted; after 5 the code is destroyed (429).
        boolean valid = otpGuard.verify("verify-account:", email, code, true);

        if (valid) {
            otpGuard.markEmailVerified(email); // sign-up for this address is now allowed
            return new ApiResponse(1, 200, null, new HashMap<>(), "Account verified successfully.");
        } else {
            return new ApiResponse(0, 400, null, new HashMap<>(), "Invalid or expired code.");
        }
    }

    @Override
    public ApiResponse getUserAuthData(AuthRequestDto requestDto, String token, String refreshToken) {
        boolean saveRefreshToken = true;

        UserLoginProjection userData = userRepository.findUserLoginByEmail(requestDto.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found with email " + requestDto.getEmail()));

        // E-mail verified but profile setup never finished: no token yet, send the person back to the setup.
        if (!"ADMIN".equals(userData.getRoleName()) && !hasFinishedSetup(userData.getId())) {
            return ApiResponse.builder()
                    .success(1)
                    .code(200)
                    .data(new LoginResponseDto(userData.getId(), userData.getEmail(), userData.getRoleName(),
                            null, null, null, null, false))
                    .message("Please finish setting up your profile.")
                    .build();
        }

        if ("TRAINER".equals(userData.getRoleName()) && "PENDING".equals(userData.getStatus())) {
            throw new UnauthorizedException("You can't login. Please wait for system Admin Approval.");
        }

        // Handle refresh token reuse
        UserTokenProjection tokenData = userTokenRepository.findLatestTokenByUsername(userData.getEmail())
                .orElse(null);

        if (tokenData != null) {
            long hoursBetween = ChronoUnit.HOURS.between(tokenData.getCreatedAt(), LocalDateTime.now());
            if (hoursBetween < 12) {
                saveRefreshToken = false;
                refreshToken = tokenData.getToken();
            }
        }

        if (saveRefreshToken) {
            UserToken userToken = new UserToken();
            userToken.setUsername(userData.getEmail());
            userToken.setToken(refreshToken);
            userTokenRepository.save(userToken);
        }

        // Fetch profile summary only if not ADMIN
        ProfileResponseDto profileResponse = null;
        UserDetailInfoResponseDto userDetailInfoResponseDto = null;
        if (!"ADMIN".equals(userData.getRoleName())) {
            ProfileProjection profile = profileRepository.findProfileSummaryByUserId(userData.getId())
                    .orElseThrow(
                            () -> new EntityNotFoundException("Profile not found for user ID: " + userData.getId()));
            profileResponse = new ProfileResponseDto(
                    profile.getName(),
                    profile.getNrc(),
                    profile.getPhone(),
                    profile.getDob(),
                    profile.getGender(),
                    profile.getProfilePic(),
                    profile.getAddress());

            UserDetailInfo userDetailInfo = userDetailInfoRepository
                    .findFirstByUserIdAndStatus(userData.getId(), Status.ACTIVE)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "User detail info not found for user ID: " + userData.getId()));

            userDetailInfoResponseDto = new UserDetailInfoResponseDto(
                    userDetailInfo.getWeight(),
                    userDetailInfo.getHeight(),
                    userDetailInfo.getGoal(),
                    userDetailInfo.getExperience(),
                    userDetailInfo.getSpecialization());

        }

        // Build login DTO
        LoginResponseDto loginResponse = new LoginResponseDto(
                userData.getId(),
                userData.getEmail(),
                userData.getRoleName(),
                token,
                refreshToken,
                profileResponse,
                userDetailInfoResponseDto,
                true);

        return ApiResponse.builder()
                .success(1)
                .code(200)
                .data(loginResponse)
                .message("Account Login successfully.")
                .build();
    }

    @Override
    @Transactional
    public ApiResponse getRefreshToken(String email, String refreshToken) {

        Map<String, Object> data;
        UserToken existingToken = userTokenRepository.findTopByUsernameOrderByCreatedAtDesc(email);
        if (existingToken != null) {
            userTokenRepository.deleteByUsername(email);
        }

        // Save new token
        UserToken userToken = new UserToken();
        userToken.setUsername(email);
        userToken.setToken(refreshToken);
        userTokenRepository.save(userToken);

        data = Map.of(
                "userName", email,
                "refreshToken", refreshToken);

        return ApiResponse.builder()
                .success(1)
                .code(200)
                .meta(null)
                .data(data)
                .message("Generated refresh token successfully.")
                .build();
    }
}
