package org._java_proj.gym_management_system.common.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtils {
    public static void main(String[] args) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode("Tth2722004"); // Replace "yourPassword"
        System.out.println("Encoded password: " + encodedPassword);
    }
}
