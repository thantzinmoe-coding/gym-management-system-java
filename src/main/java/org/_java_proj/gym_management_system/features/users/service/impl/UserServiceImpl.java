package org._java_proj.gym_management_system.features.users.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org._java_proj.gym_management_system.common.storage.StorageService;
import org._java_proj.gym_management_system.common.storage.StorageServiceFactory;
import org._java_proj.gym_management_system.common.util.ServerUtil;
import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.features.bmi.dto.response.BMIDetailResponseDto;
import org._java_proj.gym_management_system.features.bmi.repository.BMIRepository;
import org._java_proj.gym_management_system.features.profile.dto.response.ProfileResponseDto;
import org._java_proj.gym_management_system.features.users.dto.request.AuthRequestDto;
import org._java_proj.gym_management_system.features.users.dto.request.UserCreateRequest;
import org._java_proj.gym_management_system.features.users.dto.response.UserResponseDto;
import org._java_proj.gym_management_system.features.users.repository.ProfileRepository;
import org._java_proj.gym_management_system.features.users.repository.RoleRepository;
import org._java_proj.gym_management_system.features.users.repository.UserRepository;
import org._java_proj.gym_management_system.features.users.repository.UserTokenRepository;
import org._java_proj.gym_management_system.features.users.service.UserService;
import org._java_proj.gym_management_system.model.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final RoleRepository roleRepository;
    private final BMIRepository bmiRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    private final ServerUtil serverUtil;
    private final UserTokenRepository userTokenRepository;
    private final StringRedisTemplate redisTemplate;


    private final AuthenticationManager authenticationManager;

    private StorageService storageService;

    @Autowired
    public void setStorageService(StorageServiceFactory factory) {
        this.storageService = factory.getConfiguredStorageService();
    }


    @Transactional
    public ApiResponse createUser(UserCreateRequest request) {
        final Role role = roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new EntityNotFoundException("Role not found."));

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);

        userRepository.save(user);

        UserResponseDto dto = modelMapper.map(user, UserResponseDto.class);
        return ApiResponse.builder().success(1).code(HttpStatus.OK.value())
                .data(Map.of("currentUser", dto))
                .message("User account created Successfully.").build();
    }



    @Transactional
    public String uploadProfilePicture(final Long userId, final MultipartFile file) {
        final Profile profile = this.profileRepository.findByUser_Id(userId)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found for user ID: " + userId));

        final String filename = storageService.store(file);

        final String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/files/")
                .path(filename)
                .toUriString();

        profile.setProfilePic(fileUrl);
        this.profileRepository.save(profile);

        return fileUrl;
    }


    @Override
    public ApiResponse verifyEmail(String email) {
        try {
            serverUtil.sendCodeToEmail(email , 15 , "verifyAccountMail" , "verify-account:");
            return new ApiResponse(1, 200, null, new HashMap<>(), "Verify Email Sent successfully.");
        } catch(Exception e){
            return new ApiResponse(0, 400, null, new HashMap<>(), "Error Sending Mail");
        }
    }

    @Override
    public ApiResponse resendCode(String email) {
        try {
            serverUtil.sendCodeToEmail(email , 15 , "verifyAccountMail" , "verify-account:");
            return new ApiResponse(1, 200, null, new HashMap<>(), "Resend Mail Sent successfully.");
        } catch(Exception e){
            return new ApiResponse(0, 400, null, new HashMap<>(), "Error Sending Mail");
        }
    }


    @Override
    public ApiResponse verifyAccount(long code , String email) {
        String key = "verify-account:" + email;
        String storedCode = redisTemplate.opsForValue().get(key);
        boolean valid = storedCode != null && storedCode.equals(String.valueOf(code));

        if (valid) {
            redisTemplate.delete(key);
            return new ApiResponse(1, 200, null, new HashMap<>(), "Account verified successfully.");
        } else {
            return new ApiResponse(0, 400, null, new HashMap<>(), "Invalid or expired code.");
        }
    }



    @Override
    public ApiResponse getUserAuthData(AuthRequestDto requestDto, String token, String refreshToken) {
        boolean saveRefreshToken = true;
        User userData = userRepository.findByEmail(requestDto.getEmail());
        Profile profile = profileRepository.findByUser_Id(userData.getId()).orElseThrow(() ->
                new EntityNotFoundException("Profile not found for user ID: " + userData.getId()));
        BMI bmi = bmiRepository.findFirstByEntityId(userData.getId()).orElseThrow(() ->
                new EntityNotFoundException("BMI not found for user ID: " + userData.getId()));;
        long roleId = userData.getRole().getId();
        String roleName = userData.getRole().getName();
        UserToken tokenData = userTokenRepository.findTopByUsernameOrderByCreatedAtDesc(requestDto.getEmail());
        if(tokenData != null){
            LocalDateTime createdAt = tokenData.getCreatedAt();
            long hoursBetween = ChronoUnit.HOURS.between(createdAt, LocalDateTime.now());
            if (hoursBetween < 12) {
                saveRefreshToken = false; // Only save if more than 12 hours have passed
                refreshToken = tokenData.getToken(); // Use existing Token in db instead of creating new one.
            }
        }

        if(saveRefreshToken){
            UserToken userToken = new UserToken();
            userToken.setUsername(requestDto.getEmail());
            userToken.setToken(refreshToken); // Refresh Token
            userTokenRepository.save(userToken);
        }
        ProfileResponseDto profileResponse = modelMapper.map(profile, ProfileResponseDto.class);
        BMIDetailResponseDto bmiDetailResponseDto = modelMapper.map(bmi, BMIDetailResponseDto.class);

        Map<String, Object> data = Map.of(
                "token", token,
                "RefreshToken", refreshToken,
                "userId", userData.getId(),
                "roleId", roleId,
                "email", userData.getEmail(),
                "roleName", roleName,
                "profile",profileResponse,
                "bmi",bmiDetailResponseDto
        );
        return ApiResponse.builder()
                .success(1)
                .code(200)
                .meta(null)
                .data(data)
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
                "refreshToken", refreshToken
        );

        return ApiResponse.builder()
                .success(1)
                .code(200)
                .meta(null)
                .data(data)
                .message("Generated refresh token successfully.")
                .build();
    }
}
