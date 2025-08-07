package org._java_proj.gym_management_system.features.feedback.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.config.response.util.ResponseUtils;
import org._java_proj.gym_management_system.features.feedback.dto.request.FeedbackCreateRequest;
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
}
