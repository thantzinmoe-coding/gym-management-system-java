package org._java_proj.gym_management_system.features.feedback.service;
import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.features.feedback.dto.request.FeedbackCreateRequest;
import org._java_proj.gym_management_system.features.feedback.dto.request.FeedbackUpdateRequest;
public interface FeedbackService {
    ApiResponse createFeedback(FeedbackCreateRequest request);
    ApiResponse getFeedback(Long id);
    ApiResponse listFeedbacks();
    ApiResponse updateFeedback(Long id,FeedbackUpdateRequest request);
    ApiResponse deleteFeedback(Long id);
}