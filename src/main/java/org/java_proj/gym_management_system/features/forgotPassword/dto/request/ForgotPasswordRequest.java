package org.java_proj.gym_management_system.features.forgotPassword.dto.request;

import lombok.Data;

@Data
public class ForgotPasswordRequest {
    private String email;
    private long code;
    /** Only used by the final reset step. */
    private String newPassword;
}
