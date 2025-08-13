package org._java_proj.gym_management_system.common.util;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@AllArgsConstructor
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

}
