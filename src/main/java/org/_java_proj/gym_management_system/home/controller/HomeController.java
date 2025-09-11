package org._java_proj.gym_management_system.home.controller;

import lombok.RequiredArgsConstructor;
import org._java_proj.gym_management_system.home.service.HomeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.base.path}/home-summary")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @GetMapping("/user-count/{role}")
    public ResponseEntity<?> getUserCount(@PathVariable String role) {
        return ResponseEntity.ok(homeService.totalMembers(role));
    }

    @GetMapping("/package-count")
    public ResponseEntity<?> getGymPackageCount() {
        return ResponseEntity.ok(homeService.totalPackageCount());
    }

    @GetMapping("/review-count")
    public ResponseEntity<?> getReviewCount() {
        return ResponseEntity.ok(homeService.totalReviewCount());
    }
}
