package org.java_proj.gym_management_system.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@AllArgsConstructor
@Slf4j
public class JWTUtil {

    private final ServerUtil serverUtil;

    public String extractEmail(String token){
        return Jwts.parserBuilder()
                .setSigningKey(serverUtil.getSecretKey().getBytes(StandardCharsets.UTF_8))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String extractTokenFromRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    public boolean ValidateToken(String token, UserDetails userDetails){
        return extractEmail(token).equals(userDetails.getUsername());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(serverUtil.getSecretKey().getBytes(StandardCharsets.UTF_8))
                    .build()
                    .parseClaimsJws(token); // just parses, throws exception if invalid
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * ✅ New: Extract all claims from JWT
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(serverUtil.getSecretKey().getBytes(StandardCharsets.UTF_8))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * ✅ New: Extract userId from JWT claim
     */
    public Long extractUserId(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.get("userId", Long.class); // ✅ this now works
        } catch (Exception e) {
            log.error("Failed to extract userId from token", e);
            return null;
        }
    }

}
