package org.java_proj.gym_management_system.features.analytics.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.java_proj.gym_management_system.config.response.dto.ApiResponse;
import org.java_proj.gym_management_system.features.analytics.client.MlServiceClient.MlServiceUnavailableException;
import org.java_proj.gym_management_system.features.analytics.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.base.path}/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Churn prediction and package recommendations")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/churn/summary")
    @Operation(summary = "Churn risk overview (admin)")
    public ResponseEntity<ApiResponse> churnSummary() {
        return ResponseEntity.ok(analyticsService.getChurnSummary());
    }

    @GetMapping("/churn/members")
    @Operation(summary = "Members ranked by churn risk, with reasons (admin)")
    public ResponseEntity<ApiResponse> atRiskMembers(
            @RequestParam(defaultValue = "50") final int limit,
            @RequestParam(defaultValue = "0") final double minRisk) {
        return ResponseEntity.ok(analyticsService.getAtRiskMembers(limit, minRisk));
    }

    @GetMapping("/churn/my-members")
    @Operation(summary = "Churn risk of the logged-in trainer's members")
    public ResponseEntity<ApiResponse> myMembersAtRisk() {
        return ResponseEntity.ok(analyticsService.getMyMembersAtRisk());
    }

    @GetMapping("/churn/members/{memberId}")
    @Operation(summary = "Churn risk of one member (admin, trainer)")
    public ResponseEntity<ApiResponse> memberChurn(@PathVariable final Long memberId) {
        return ResponseEntity.ok(analyticsService.getMemberChurn(memberId));
    }

    @GetMapping("/recommendations/me")
    @Operation(summary = "Recommended packages for the logged-in member")
    public ResponseEntity<ApiResponse> myRecommendations(
            @RequestParam(defaultValue = "3") final int limit) {
        return ResponseEntity.ok(analyticsService.getMyRecommendations(limit));
    }

    @GetMapping("/recommendations/{memberId}")
    @Operation(summary = "Recommended packages for a member (admin, trainer)")
    public ResponseEntity<ApiResponse> recommendations(
            @PathVariable final Long memberId,
            @RequestParam(defaultValue = "3") final int limit) {
        return ResponseEntity.ok(analyticsService.getRecommendations(memberId, limit));
    }

    @GetMapping("/model")
    @Operation(summary = "Current churn model version and test metrics (admin)")
    public ResponseEntity<ApiResponse> modelInfo() {
        return ResponseEntity.ok(analyticsService.getModelInfo());
    }

    @PostMapping("/model/retrain")
    @Operation(summary = "Retrain the churn model on the current data (admin)")
    public ResponseEntity<ApiResponse> retrain() {
        return ResponseEntity.ok(analyticsService.retrainModel());
    }

    @ExceptionHandler(MlServiceUnavailableException.class)
    public ResponseEntity<ApiResponse> handleMlUnavailable(final MlServiceUnavailableException ex) {
        final int status = ex.getStatus() >= 500 || ex.getStatus() == 409 ? ex.getStatus() : 502;
        return ResponseEntity.status(status).body(ApiResponse.builder()
                .success(0)
                .code(status)
                .message(ex.getMessage())
                .build());
    }
}
