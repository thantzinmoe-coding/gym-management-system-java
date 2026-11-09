package org.java_proj.gym_management_system.features.forgotPassword.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.java_proj.gym_management_system.common.util.OtpGuard;
import org.java_proj.gym_management_system.common.util.ServerUtil;
import org.java_proj.gym_management_system.features.forgotPassword.service.ForgotPasswordService;
import org.java_proj.gym_management_system.features.users.repository.UserRepository;
import org.java_proj.gym_management_system.features.users.repository.UserTokenRepository;
import org.java_proj.gym_management_system.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ForgotPasswordServiceImpl implements ForgotPasswordService {

    private static final String PREFIX = "forgot-password:";
    private static final long EXPIRATION_MINUTES = 15;
    private static final int MIN_PASSWORD_LENGTH = 8;

    private final ServerUtil serverUtil;
    private final OtpGuard otpGuard;
    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void sendResetCode(String email) {
        otpGuard.checkResendCooldown(PREFIX, email);
        // Same response whether or not the account exists, so this can't be used to
        // discover which emails are registered.
        if (userRepository.existsByEmail(email)) {
            serverUtil.sendCodeToEmail(email, EXPIRATION_MINUTES, "resetMail", PREFIX);
        } else {
            log.info("Password reset requested for an unknown email");
        }
    }

    @Override
    public boolean verifyResetCode(String email, long code) {
        return otpGuard.verify(PREFIX, email, code, false);
    }

    @Override
    @Transactional
    public void resetPassword(String email, long code, String newPassword) {
        if (newPassword == null || newPassword.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("The new password must be at least " + MIN_PASSWORD_LENGTH + " characters.");
        }
        if (!otpGuard.verify(PREFIX, email, code, true)) {
            throw new IllegalArgumentException("Invalid or expired code.");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired code."));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        // Revoke stored refresh tokens so sessions opened with the old password end.
        userTokenRepository.deleteByUsername(email);
    }
}
