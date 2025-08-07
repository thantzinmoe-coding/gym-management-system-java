package org._java_proj.gym_management_system.config.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Creates the PasswordEncoder bean.
     * This is required by Spring Security for encoding and verifying passwords.
     * Fixes the "APPLICATION FAILED TO START" error.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the security filter chain.
     * This is where you define which endpoints are public and which are protected.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF protection, which is common for stateless REST APIs.
                .csrf(AbstractHttpConfigurer::disable)

                // Define authorization rules for HTTP requests.
                .authorizeHttpRequests(auth -> auth
                        // --- PUBLIC ENDPOINTS ---
                        // Allow POST requests to the feedback endpoint without authentication.
                        // This fixes the original 404 Not Found error.
                        .requestMatchers(HttpMethod.POST, "/api/v1/feedback").permitAll()

                        // Allow access to authentication endpoints (e.g., login, register).
                        // You'll likely need this for your UserController.
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/v1/users/**").permitAll() // Or maybe this path

                        // Allow access to Swagger UI and OpenAPI docs.
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()

                        // --- PROTECTED ENDPOINTS ---
                        // All other requests must be authenticated.
                        .anyRequest().authenticated()
                )

                // Configure session management to be stateless.
                // This is standard for REST APIs, as they shouldn't rely on server sessions.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // If you are using JWT authentication, you would add your custom JWT filter here.
        // For example: .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}