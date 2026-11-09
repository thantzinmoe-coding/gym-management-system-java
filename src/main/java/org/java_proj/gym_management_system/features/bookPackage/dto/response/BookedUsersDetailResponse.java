package org.java_proj.gym_management_system.features.bookPackage.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookedUsersDetailResponse {
    private Long id;
    private String name;
    private String email;
    private String nrc;
    private String phone;
    private String dob;
    private String gender;
    private Double height;
    private Double weight;
    private String goal;
    private String avatarUrl;
}
