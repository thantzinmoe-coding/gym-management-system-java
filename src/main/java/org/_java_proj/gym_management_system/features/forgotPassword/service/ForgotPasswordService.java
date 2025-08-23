package org._java_proj.gym_management_system.features.forgotPassword.service;

public interface ForgotPasswordService {
    void sendResetCode(String email);

    boolean verifyResetCode(String email, long code);
}
