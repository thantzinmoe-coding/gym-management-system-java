package org.java_proj.gym_management_system.features.bookPackage.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookPackageRequest {
    @NotNull(message = "Member ID is required")
    private Long memberID;

    @NotNull(message = "Gym package ID is required")
    private Long gymPackageID;

}
