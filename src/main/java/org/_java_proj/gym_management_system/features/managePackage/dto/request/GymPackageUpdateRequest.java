package org._java_proj.gym_management_system.features.managePackage.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org._java_proj.gym_management_system.common.constant.GymPackageType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response object for a class group")
public class GymPackageUpdateRequest {
    @NotNull(message = "Package name is required")
    @Schema(description = "Gym Package Name", example = "Personal package")
    private String name;

    @NotNull(message = "Description is required")
    @Schema(description = "Gym Package Description", example = "premium package for gym management system")
    private String description;

    @NotNull(message = "Gym package type is required")
    @Schema(description = "Gym Package Type", example = "PERSONAL")
    private GymPackageType gymPackageType;

    @NotNull(message = "Package price is required")
    @Schema(description = "Gym Package Price", example = "65000")
    private double price;

    @NotNull(message = "Duration is required")
    @Schema(description = "Gym Package Duration", example = "3 Months")
    private String duration;

}
