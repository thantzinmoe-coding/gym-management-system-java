package org._java_proj.gym_management_system.features.bookPackage.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org._java_proj.gym_management_system.common.constant.MemberStatus;

@Data
public class BookPackageRequest {
    @NotBlank(message = "Member ID is required")
    private Long memberID;

    @NotBlank(message = "Gym package ID is required")
    private Long gymPackageID;

}
