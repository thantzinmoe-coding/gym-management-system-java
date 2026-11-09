package org.java_proj.gym_management_system.features.analytics.service;

import org.java_proj.gym_management_system.config.response.dto.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;

public interface AnalyticsService {

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    ApiResponse getChurnSummary();

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    ApiResponse getAtRiskMembers(int limit, double minRisk);

    @PreAuthorize("hasRole('ROLE_TRAINER')")
    ApiResponse getMyMembersAtRisk();

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TRAINER')")
    ApiResponse getMemberChurn(Long memberId);

    @PreAuthorize("hasRole('ROLE_MEMBER')")
    ApiResponse getMyRecommendations(int limit);

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TRAINER')")
    ApiResponse getRecommendations(Long memberId, int limit);

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    ApiResponse getModelInfo();

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    ApiResponse retrainModel();
}
