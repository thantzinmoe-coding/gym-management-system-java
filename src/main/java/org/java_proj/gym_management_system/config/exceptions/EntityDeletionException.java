package org.java_proj.gym_management_system.config.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class EntityDeletionException extends RuntimeException {
    public EntityDeletionException(String message) { super(message); }
}
