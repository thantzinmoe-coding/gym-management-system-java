package org.java_proj.gym_management_system.common.util;

import lombok.RequiredArgsConstructor;
import org.java_proj.gym_management_system.features.users.repository.UserRepository;
import org.java_proj.gym_management_system.model.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * The authenticated caller, for ownership checks: a non-admin may only read or change
 * records that belong to them. Violations throw {@link AccessDeniedException} (HTTP 403).
 */
@Component
@RequiredArgsConstructor
public class CurrentUser {

    private final UserRepository userRepository;

    public User get() {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new AccessDeniedException("Authentication required.");
        }
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new AccessDeniedException("Authentication required."));
    }

    public Long id() {
        return get().getId();
    }

    public boolean hasRole(final String role) {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }

    public boolean isAdmin() {
        return hasRole("ADMIN");
    }

    /** True when the caller is the given user or an admin. */
    public boolean isSelfOrAdmin(final Long userId) {
        return isAdmin() || Objects.equals(id(), userId);
    }

    /** Allows only the given user themselves (for private data such as chats). */
    public void requireSelf(final Long userId) {
        if (!Objects.equals(id(), userId)) {
            throw new AccessDeniedException("You can only access your own data.");
        }
    }

    /** Allows the given user themselves, or an admin; everyone else gets 403. */
    public void requireSelfOrAdmin(final Long userId) {
        if (!isSelfOrAdmin(userId)) {
            throw new AccessDeniedException("You can only access your own data.");
        }
    }
}
