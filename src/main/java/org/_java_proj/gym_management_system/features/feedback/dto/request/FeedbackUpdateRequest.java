package org._java_proj.gym_management_system.features.feedback.dto.request;

import java.time.LocalDate;

import lombok.Data;
@Data
public class FeedbackUpdateRequest {
    private String comment;
    private Integer ratingPoints;
    private LocalDate date;

    // If you want to allow updating member/trainer, add their IDs too
}
