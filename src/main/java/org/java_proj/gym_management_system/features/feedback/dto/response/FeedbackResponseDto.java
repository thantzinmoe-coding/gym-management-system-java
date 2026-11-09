package org.java_proj.gym_management_system.features.feedback.dto.response;



import lombok.Data;

import java.time.LocalDate;

@Data
public class FeedbackResponseDto {

    private Long memberId;
    private Long trainerId;
    private String comment;
    private int ratingPoints;
    private LocalDate date;
    // Field name used in the latest code
}