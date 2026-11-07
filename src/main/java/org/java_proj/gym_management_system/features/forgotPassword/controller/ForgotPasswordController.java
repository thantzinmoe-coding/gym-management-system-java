package org.java_proj.gym_management_system.features.forgotPassword.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.java_proj.gym_management_system.config.response.dto.ApiResponse;
import org.java_proj.gym_management_system.config.response.util.ResponseUtils;
import org.java_proj.gym_management_system.features.forgotPassword.dto.request.ForgotPasswordRequest;
import org.java_proj.gym_management_system.features.forgotPassword.service.ForgotPasswordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * Password reset for signed-out users: request a code by email, (optionally) check it,
 * then set a new password with it. All three are public; codes are rate- and attempt-limited.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base.path}/forgotPassword")
@Tag(name = "Forgot password", description = "Reset a password with an emailed code")
public class ForgotPasswordController {

    private final ForgotPasswordService forgotPasswordService;

    @PostMapping
    @Operation(summary = "Email a reset code", description = "Body: { \"email\": \"user@example.com\" }")
    public ResponseEntity<ApiResponse> sendResetCode(@RequestBody ForgotPasswordRequest body, HttpServletRequest request) {
        forgotPasswordService.sendResetCode(body.getEmail());
        return ResponseUtils.buildResponse(request, ok("If an account exists for this email, a reset code has been sent."));
    }

    @PostMapping("/verifyCode")
    @Operation(summary = "Check a reset code without using it", description = "Body: { \"email\": \"...\", \"code\": 123456 }")
    public ResponseEntity<ApiResponse> verifyResetCode(@RequestBody ForgotPasswordRequest body, HttpServletRequest request) {
        if (forgotPasswordService.verifyResetCode(body.getEmail(), body.getCode())) {
            return ResponseUtils.buildResponse(request, ok("Code is valid."));
        }
        return ResponseUtils.buildResponse(request, error("Invalid or expired code."));
    }

    @PostMapping("/reset")
    @Operation(summary = "Set a new password", description = "Body: { \"email\": \"...\", \"code\": 123456, \"newPassword\": \"...\" }")
    public ResponseEntity<ApiResponse> resetPassword(@RequestBody ForgotPasswordRequest body, HttpServletRequest request) {
        forgotPasswordService.resetPassword(body.getEmail(), body.getCode(), body.getNewPassword());
        return ResponseUtils.buildResponse(request, ok("Password updated. You can now sign in with your new password."));
    }

    private static ApiResponse ok(String message) {
        return new ApiResponse(1, HttpStatus.OK.value(), null, new HashMap<>(), message);
    }

    private static ApiResponse error(String message) {
        return new ApiResponse(0, HttpStatus.BAD_REQUEST.value(), null, new HashMap<>(), message);
    }
}
