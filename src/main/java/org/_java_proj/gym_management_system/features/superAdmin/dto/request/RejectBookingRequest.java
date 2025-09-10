package org._java_proj.gym_management_system.features.superAdmin.dto.request;

import lombok.Data;

@Data
public class RejectBookingRequest {
    private String email;
    private String packageName;
    private String name;
}
