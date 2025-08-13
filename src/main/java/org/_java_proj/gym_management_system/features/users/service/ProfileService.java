package org._java_proj.gym_management_system.features.users.service;

import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.features.profile.dto.request.ProfileRequestDto;
import org.springframework.web.multipart.MultipartFile;

public interface ProfileService {
    public String uploadProfilePicture(final Long userId, final MultipartFile file);

    public ApiResponse createProfile(final Long userId, final ProfileRequestDto profileRequest, final MultipartFile file);

    public ApiResponse softDeleteProfile(final Long userId);

    public ApiResponse updateProfile(final Long userId,final ProfileRequestDto profileRequest);

    public ApiResponse getProfileById(final Long userId);
}
