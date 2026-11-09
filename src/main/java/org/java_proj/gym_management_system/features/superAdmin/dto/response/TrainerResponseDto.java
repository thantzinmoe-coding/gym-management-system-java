package org.java_proj.gym_management_system.features.superAdmin.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrainerResponseDto {
    private Long id;

    private String name;
    private String email;
    private String phone;
    private String specialization;
    private String experience;
    private String avatarUrl;
    private String status;
    // Add other relevant trainer fields here (e.g., phone number, specialization)
}
