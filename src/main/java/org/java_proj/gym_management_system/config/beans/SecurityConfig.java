package org.java_proj.gym_management_system.config.beans;

import org.java_proj.gym_management_system.common.middleware.JWTFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private static final String[] SWAGGER_WHITELIST = {
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/swagger-resources"
    };

    // Public sign-up / sign-in calls. Everything else under /api/v1/auth needs a login.
    private static final String[] PUBLIC_AUTH_POSTS = {
            "/api/v1/auth/users",
            "/api/v1/auth/users/login",
            "/api/v1/auth/users/verifyEmail",
            "/api/v1/auth/users/verifyAccount",
            "/api/v1/auth/users/resendCode",
            "/api/v1/auth/users/getRefreshToken",
            "/api/v1/auth/users/extractToken",
            // password reset for signed-out users (rate- and attempt-limited by OtpGuard)
            "/api/v1/forgotPassword",
            "/api/v1/forgotPassword/verifyCode",
            "/api/v1/forgotPassword/reset"
    };

    // Profile setup happens right after sign-up, before the first login. Both services only
    // allow creating these once per user.
    private static final String[] SIGNUP_PROFILE_POSTS = {
            "/api/v1/auth/profile/*/create",
            "/api/v1/auth/user-detail-info/*"
    };

    // Read-only data shown on the public pages (home, reviews, equipment).
    private static final String[] PUBLIC_READS = {
            "/api/v1/home-summary/**",
            "/api/v1/feedback", "/api/v1/feedback/**",
            "/api/v1/equipment", "/api/v1/equipment/**"
    };

    // Trainer lists that members browse; the rest of /super_admin is admin-only.
    private static final String[] TRAINER_LISTS = {
            "/api/v1/super_admin/all-trainers",
            "/api/v1/super_admin/all-active-trainers",
            "/api/v1/super_admin/all-available-trainers"
    };

    @Autowired
    private JWTFilter jwtAuthFilter;

    @Autowired
    private UserDetailsService userDetailService;

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userDetailService);
        return daoAuthenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable).
                authorizeHttpRequests(auth -> auth
                        // --- public ---
                        .requestMatchers(SWAGGER_WHITELIST).permitAll()
                        .requestMatchers("/webjars/**", "/ws/**").permitAll() // STOMP frames are authenticated by JwtWebSocketAuthInterceptor
                        .requestMatchers(HttpMethod.POST, PUBLIC_AUTH_POSTS).permitAll()
                        .requestMatchers(HttpMethod.POST, SIGNUP_PROFILE_POSTS).permitAll()
                        .requestMatchers(HttpMethod.GET, PUBLIC_READS).permitAll()
                        // --- admin only ---
                        .requestMatchers(TRAINER_LISTS).authenticated()
                        .requestMatchers("/api/v1/super_admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/salaries", "/api/v1/salaries/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/gym-package", "/api/v1/gym-package/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/gym-package/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/gym-package/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/schedule/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/schedule/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/schedule/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/equipment", "/api/v1/equipment/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/equipment/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/equipment/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/feedback/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/feedback/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/notifications/send").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/assign-gym-package", "/api/v1/assign-gym-package/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/assign-gym-package/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/assign-gym-package/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/auth/user-detail-info/*").hasRole("ADMIN")
                        .requestMatchers("/api/v1/attendance/trainer/*/total-hours").hasRole("ADMIN")
                        // trainers and members read their own attendance (ownership checked in the controller)
                        .requestMatchers(HttpMethod.GET, "/api/v1/attendance/trainer/*").hasAnyRole("TRAINER", "MEMBER", "ADMIN")
                        .requestMatchers("/api/v1/attendance", "/api/v1/attendance/**").hasRole("ADMIN")
                        // --- role-specific ---
                        .requestMatchers(HttpMethod.POST, "/api/v1/feedback").hasRole("MEMBER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/assign-gym-package/assign-package-by-trainer/*").hasAnyRole("TRAINER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/book-package/*/get-by-package-id").hasAnyRole("TRAINER", "ADMIN")
                        // everything else (profiles, chat, notifications, bookings, ...) needs a login;
                        // ownership of user-specific records is checked with CurrentUser
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()));
        return http.build();
    }

    // Browser origins allowed to call the API (app.cors.allowed-origins / APP_CORS_ALLOWED_ORIGINS).
    @org.springframework.beans.factory.annotation.Value("${app.cors.allowed-origins}")
    private String[] allowedOrigins;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(allowedOrigins));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE","PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
