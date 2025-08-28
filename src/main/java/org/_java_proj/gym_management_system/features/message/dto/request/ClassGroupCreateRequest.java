package org._java_proj.gym_management_system.features.message.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request to create a new class group")
public class ClassGroupCreateRequest {

    @Schema(description = "Group name", example = "Math Study Group")
    private String name;

    @Schema(description = "Maximum number of members allowed in the group", example = "20", defaultValue = "20")
    private Integer maxMembers = 20;

    @Schema(description = "Whether the group is private", example = "false", defaultValue = "false")
    private Boolean isPrivate = false;

    @Schema(description = "Meeting schedule details", example = "Every Monday at 5 PM")
    private String meetingSchedule;

    @Schema(description = "Image URL representing the group", example = "https://example.com/images/math-group.png")
    private String groupImageUrl;
}

