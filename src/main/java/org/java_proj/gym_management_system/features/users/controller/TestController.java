package org.java_proj.gym_management_system.features.users.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class TestController {

    @GetMapping("/api/v1/users/me")
    public String getMyPrincipalName(Principal principal) {
        if (principal != null) {
            // This will return the exact name you need to use in convertAndSendToUser
            return principal.getName();
        }
        return "Principal is null";
    }
}