package org._java_proj.gym_management_system.features.feedback.service;
import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.features.feedback.dto.request.FeedbackCreateRequest;

public interface FeedbackService {
    ApiResponse createFeedback(FeedbackCreateRequest request);
}