package org._java_proj.gym_management_system.features.feedback.service;
import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.config.response.dto.PaginatedApiResponse;
import org._java_proj.gym_management_system.features.feedback.dto.request.FeedbackCreateRequest;
import org._java_proj.gym_management_system.features.feedback.dto.request.FeedbackUpdateRequest;
import org._java_proj.gym_management_system.features.feedback.dto.response.FeedbackResponseDto;
import org.springframework.data.domain.Pageable;

public interface FeedbackService {
    ApiResponse createFeedback(FeedbackCreateRequest request);
    ApiResponse getFeedback(Long id);

    ApiResponse updateFeedback(Long id, FeedbackUpdateRequest request);
    ApiResponse deleteFeedback(Long id);
    PaginatedApiResponse<FeedbackResponseDto> listFeedbacks(Pageable pageable);
    ApiResponse getAverageRatingByTrainer(Long trainerId); // New method (renamed)
}