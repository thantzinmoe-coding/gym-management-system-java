package org._java_proj.gym_management_system.features.users.service;

import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.features.users.dto.request.AuthRequestDto;
import org._java_proj.gym_management_system.features.users.dto.request.UserCreateRequest;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    public ApiResponse createUser(UserCreateRequest request);

    ApiResponse verifyEmail(String email);

    public ApiResponse resendCode(String email);

    String uploadProfilePicture(final Long userId, final MultipartFile file);

    public ApiResponse verifyAccount(long code , String email);

    ApiResponse getUserAuthData(AuthRequestDto requestDto, String token, String refreshToken);

    ApiResponse getRefreshToken(String email, String refreshToken);

}
