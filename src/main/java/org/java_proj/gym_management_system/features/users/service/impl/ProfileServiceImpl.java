package org.java_proj.gym_management_system.features.users.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.java_proj.gym_management_system.common.storage.StorageService;
import org.java_proj.gym_management_system.common.storage.StorageServiceFactory;
import org.java_proj.gym_management_system.config.response.dto.ApiResponse;
import org.java_proj.gym_management_system.features.profile.dto.request.ProfileRequestDto;
import org.java_proj.gym_management_system.features.profile.dto.response.ProfileResponseDto;
import org.java_proj.gym_management_system.features.users.repository.ProfileRepository;
import org.java_proj.gym_management_system.features.users.repository.UserRepository;
import org.java_proj.gym_management_system.features.users.service.ProfileService;
import org.java_proj.gym_management_system.model.Profile;
import org.java_proj.gym_management_system.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final ModelMapper modelMapper;
    private StorageService storageService;

    @Autowired
    public void setStorageService(StorageServiceFactory factory) {
        this.storageService = factory.getConfiguredStorageService();
    }



    @Transactional
    public String uploadProfilePicture(final Long userId, final MultipartFile file) {
        final Profile profile = this.profileRepository.findByUser_Id(userId)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found for user ID: " + userId));


        if (file != null && !file.isEmpty()) {
            String filename = storageService.store(file);
            String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/files/")
                    .path(filename)
                    .toUriString();
            profile.setProfilePic(fileUrl);
            this.profileRepository.save(profile);
            return fileUrl;

        } else {
            return profile.getProfilePic();
        }

    }

    @Transactional
    @Override
    public ApiResponse createProfile(final Long userId, final ProfileRequestDto profileRequest, final  MultipartFile file) {

        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Profile profile = modelMapper.map(profileRequest, Profile.class);


        if (file != null && !file.isEmpty()) {
            String filename = storageService.store(file);
            String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/files/")
                    .path(filename)
                    .toUriString();
            profile.setProfilePic(fileUrl);
        } else {
            profile.setProfilePic(null); // or set a default image URL if you want
        }


        profile.setUser(user);

        this.profileRepository.save(profile);

        ProfileResponseDto response = modelMapper.map(profile, ProfileResponseDto.class);

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.CREATED.value())
                .meta(null)
                .data(response)
                .message("Profile created successfully")
                .build();
    }

    @Transactional
    @Override
    public ApiResponse softDeleteProfile(final Long userId) {
        Profile profile = this.profileRepository.findByUser_Id(userId)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found for user ID: " + userId));

        profile.delete();
        profile.setPhone(null);
        profile.setNrc(null);
        this.profileRepository.save(profile);
        return ApiResponse.builder()
                .success(1)
                .code(200)
                .data(null)
                .meta(null)
                .message("Profile soft-deleted successfully")
                .build();
    }

    @Transactional
    @Override
    public ApiResponse updateProfile(final Long userId, final ProfileRequestDto profileRequest) {
        Profile profile = this.profileRepository.findByUser_Id(userId).orElseThrow(() -> new EntityNotFoundException("Profile not found for user ID: " + userId));

        modelMapper.map(profileRequest,profile);


        this.profileRepository.save(profile);

        ProfileResponseDto response = modelMapper.map(profile, ProfileResponseDto.class);

        return ApiResponse.builder()
                .success(1)
                .code(200)
                .meta(null)
                .data(response)
                .message("Profile updated successfully")
                .build();
    }

    @Override
    public ApiResponse getProfileById(final Long userId) {
        Profile profile = this.profileRepository.findByUser_Id(userId).orElseThrow(() -> new EntityNotFoundException("Profile not found for user ID: " + userId));

        ProfileResponseDto response = modelMapper.map(profile, ProfileResponseDto.class);

        return ApiResponse.builder()
                .success(1)
                .code(200)
                .data(response)
                .message("Profile fetched successfully")
                .build();
    }
}
