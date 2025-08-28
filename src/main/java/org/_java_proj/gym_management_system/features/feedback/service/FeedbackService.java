package org._java_proj.gym_management_system.features.feedback.service;
import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.config.response.dto.PaginatedApiResponse;
import org._java_proj.gym_management_system.features.feedback.dto.request.FeedbackCreateRequest;
import org._java_proj.gym_management_system.features.feedback.dto.request.FeedbackUpdateRequest;
import org._java_proj.gym_management_system.features.feedback.dto.response.FeedbackResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

public interface FeedbackService {
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    ApiResponse createFeedback(FeedbackCreateRequest request);

    ApiResponse getFeedback(Long id);

    PaginatedApiResponse<FeedbackResponseDto> listFeedbacks(Pageable pageable);

    @PreAuthorize("hasRole('ROLE_MEMBER')")
    ApiResponse updateFeedback(Long id,FeedbackUpdateRequest request);

    @PreAuthorize("hasRole('ROLE_MEMBER')")
    ApiResponse deleteFeedback(Long id);

    ApiResponse getAverageRatingByTrainer(Long trainerId);
}