package org.java_proj.gym_management_system.common.util;

import lombok.RequiredArgsConstructor;
import org.java_proj.gym_management_system.config.exceptions.TooManyRequestsException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Brute-force protection for the 6-digit email codes (account verification, password reset).
 *
 * <ul>
 *   <li>at most {@value #MAX_ATTEMPTS} wrong guesses per code; then the code is destroyed</li>
 *   <li>at most one new code per address every {@value #RESEND_COOLDOWN_SECONDS} seconds</li>
 *   <li>a successful account verification leaves a short-lived "verified" mark that sign-up
 *       requires, so accounts can only be created for addresses the person controls</li>
 * </ul>
 * Codes live in Redis under {@code prefix + email} (see {@link ServerUtil#sendCodeToEmail}).
 */
@Component
@RequiredArgsConstructor
public class OtpGuard {

    public static final int MAX_ATTEMPTS = 5;
    public static final int RESEND_COOLDOWN_SECONDS = 60;
    private static final Duration VERIFIED_MARK_TTL = Duration.ofMinutes(30);

    private final StringRedisTemplate redis;

    /** Throws if a code was sent to this address within the cooldown window. */
    public void checkResendCooldown(final String prefix, final String email) {
        final Boolean first = redis.opsForValue().setIfAbsent(
                "otp-cooldown:" + prefix + email, "1", Duration.ofSeconds(RESEND_COOLDOWN_SECONDS));
        if (!Boolean.TRUE.equals(first)) {
            throw new TooManyRequestsException(
                    "A code was just sent. Please wait a minute before requesting another one.");
        }
    }

    /** Forget earlier failed attempts (call when a new code is issued). */
    public void resetAttempts(final String prefix, final String email) {
        redis.delete("otp-attempts:" + prefix + email);
    }

    /**
     * Checks {@code code} against the stored one. Wrong guesses are counted; after
     * {@value #MAX_ATTEMPTS} the code is deleted and a {@link TooManyRequestsException} is thrown.
     *
     * @param consume delete the code when it matches (false = just check, e.g. a preview step)
     */
    public boolean verify(final String prefix, final String email, final long code, final boolean consume) {
        final String codeKey = prefix + email;
        final String attemptsKey = "otp-attempts:" + prefix + email;
        final String stored = redis.opsForValue().get(codeKey);
        if (stored == null) {
            return false;
        }
        if (stored.equals(String.format("%06d", code)) || stored.equals(String.valueOf(code))) {
            if (consume) {
                redis.delete(codeKey);
                redis.delete(attemptsKey);
            }
            return true;
        }
        final Long attempts = redis.opsForValue().increment(attemptsKey);
        redis.expire(attemptsKey, Duration.ofMinutes(15));
        if (attempts != null && attempts >= MAX_ATTEMPTS) {
            redis.delete(codeKey);
            redis.delete(attemptsKey);
            throw new TooManyRequestsException("Too many wrong codes. Please request a new code.");
        }
        return false;
    }

    public void markEmailVerified(final String email) {
        redis.opsForValue().set("verified-email:" + email, "1", VERIFIED_MARK_TTL);
    }

    public boolean isEmailVerified(final String email) {
        return Boolean.TRUE.equals(redis.hasKey("verified-email:" + email));
    }

    public void clearEmailVerified(final String email) {
        redis.delete("verified-email:" + email);
    }
}
