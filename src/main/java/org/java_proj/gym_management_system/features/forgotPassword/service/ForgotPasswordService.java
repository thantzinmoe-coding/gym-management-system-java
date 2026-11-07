package org.java_proj.gym_management_system.features.forgotPassword.service;

public interface ForgotPasswordService {
    /** Emails a reset code if the account exists (silently does nothing otherwise). */
    void sendResetCode(String email);

    /** Checks a code without using it up (attempt-limited). */
    boolean verifyResetCode(String email, long code);

    /** Checks and uses up the code, then sets the new password and signs out other sessions. */
    void resetPassword(String email, long code, String newPassword);
}
