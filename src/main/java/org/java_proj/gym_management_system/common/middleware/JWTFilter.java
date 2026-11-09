package org.java_proj.gym_management_system.common.middleware;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.java_proj.gym_management_system.common.util.JWTUtil;
import org.java_proj.gym_management_system.features.users.service.impl.UserDetailServiceImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final UserDetailServiceImpl userDetailService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader=request.getHeader("Authorization");
        final String token;
        final String email;

        if (authHeader ==null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }
        token =authHeader.substring(7);
        try {
            email=jwtUtil.extractEmail(token);
        } catch (io.jsonwebtoken.JwtException | IllegalArgumentException e) {
            // Expired or invalid token: continue unauthenticated. Public routes (login,
            // register, refresh) still work; protected routes are rejected by Spring Security.
            // Throwing here used to make even the login request fail with 403 whenever the
            // browser still held an expired token cookie.
            filterChain.doFilter(request,response);
            return;
        }

        if (email !=null && SecurityContextHolder.getContext().getAuthentication()==null) {
            UserDetails userDetails = userDetailService.loadUserByUsername(email);

            if (jwtUtil.ValidateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken
                        = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request,response);
    }
}
