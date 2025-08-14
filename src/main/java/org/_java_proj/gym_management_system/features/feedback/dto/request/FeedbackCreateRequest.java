package org._java_proj.gym_management_system.features.feedback.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class FeedbackCreateRequest {

    private String comment; // Field name used in the latest code
    private int ratingPoints;
    private Long member_id;
    private Long trainer_id;
    private LocalDate date;

}
