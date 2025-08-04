package org._java_proj.gym_management_system.features.users.service;

import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.features.users.dto.request.UserCreateRequest;

public interface UserService {
    public ApiResponse createUser(UserCreateRequest request);
}
