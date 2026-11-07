package org.java_proj.gym_management_system.config.exceptions;

/** Thrown when a caller is rate-limited (e.g. too many wrong one-time codes). Maps to HTTP 429. */
public class TooManyRequestsException extends RuntimeException {
    public TooManyRequestsException(String message) {
        super(message);
    }
}
