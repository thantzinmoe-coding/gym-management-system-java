package org.java_proj.gym_management_system.features.profile.util;

import org.java_proj.gym_management_system.config.response.dto.ApiResponse;

public class ApiErrorResponse {
    public static ApiResponse error(int statusCode, String message) {
        return ApiResponse.builder()
                .success(0)
                .code(statusCode)
                .meta(null)
                .data(null)
                .message(message)
                .build();
    }
}
