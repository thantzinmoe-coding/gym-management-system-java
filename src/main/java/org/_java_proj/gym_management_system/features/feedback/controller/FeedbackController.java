package org._java_proj.gym_management_system.features.feedback.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.config.response.util.ResponseUtils;
import org._java_proj.gym_management_system.features.feedback.dto.request.FeedbackCreateRequest;
import org._java_proj.gym_management_system.features.feedback.dto.request.FeedbackUpdateRequest;
import org._java_proj.gym_management_system.features.feedback.service.FeedbackService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base.path}/feedback")
@Tag(name = "Feedback API", description = "Endpoints for managing feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    @Operation(summary = "Submit new feedback", description = "Allows users to submit feedback.")
    public ResponseEntity<ApiResponse> createFeedback(
            @RequestBody final FeedbackCreateRequest feedbackRequest,
            final HttpServletRequest request
    ) {
        final ApiResponse response = this.feedbackService.createFeedback(feedbackRequest);
        return ResponseUtils.buildResponse(request, response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Feedback by ID", description = "Fetch a single feedback entry by ID")
    public ResponseEntity<ApiResponse> getFeedback(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        final ApiResponse response = feedbackService.getFeedback(id);
        return ResponseUtils.buildResponse(request, response);
    }
    @GetMapping
    @Operation(summary = "List all feedback", description = "Fetch all feedback entries")
    public ResponseEntity<ApiResponse> listFeedbacks(HttpServletRequest request) {
        final ApiResponse response = feedbackService.listFeedbacks();
        return ResponseUtils.buildResponse(request, response);
    }
    @PatchMapping("/{id}")
    @Operation(summary = "Update Feedback", description = "Update an existing feedback entry")
    public ResponseEntity<ApiResponse> updateFeedback(
            @PathVariable Long id,
            @RequestBody FeedbackUpdateRequest updateRequest,
            HttpServletRequest request
    ) {
        final ApiResponse response = feedbackService.updateFeedback(id, updateRequest);
        return ResponseUtils.buildResponse(request, response);
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Feedback", description = "Delete a feedback entry by ID")
    public ResponseEntity<ApiResponse> deleteFeedback(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        final ApiResponse response = feedbackService.deleteFeedback(id);
        return ResponseUtils.buildResponse(request, response);
    }

    @GetMapping("/trainer/{trainerId}/average-rating")  // New endpoint
    @Operation(summary = "Get Average Rating for Trainer", description = "Fetch the average rating for a specific trainer")
    public ResponseEntity<ApiResponse> getAverageRatingForTrainer(
            @PathVariable Long trainerId,
            HttpServletRequest request
    ) {
        final ApiResponse response = feedbackService.getAverageRatingByTrainer(trainerId);
        return ResponseUtils.buildResponse(request, response);
    }
}



