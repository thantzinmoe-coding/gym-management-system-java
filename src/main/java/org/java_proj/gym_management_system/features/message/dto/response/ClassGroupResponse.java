package org.java_proj.gym_management_system.features.message.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response object for a class group")
public class ClassGroupResponse {

    @Schema(description = "Unique identifier of the group", example = "1")
    private Long id;

    @Schema(description = "Group name", example = "Math Study Group")
    private String name;

    @Schema(description = "Maximum number of members allowed", example = "20")
    private Integer maxMembers;

    @Schema(description = "Number of members currently in the group", example = "5")
    private Integer currentMembers;

    @Schema(description = "Whether the group is private", example = "false")
    private Boolean isPrivate;

    @Schema(description = "Schedule for group meetings", example = "Every Monday at 5 PM")
    private String meetingSchedule;

    @Schema(description = "URL of the group image", example = "https://example.com/images/math-group.png")
    private String groupImageUrl;

    @Schema(description = "Date and time when the group was created", example = "2025-08-26T12:34:56")
    private LocalDateTime createdAt;

    // Creator info
    @Schema(description = "Name of the group creator", example = "Alice Johnson")
    private String creatorName;

    @Schema(description = "Email of the group creator", example = "alice@example.com")
    private String creatorEmail;

    // User status
    @Schema(description = "Whether the current user is a member of this group", example = "true")
    private Boolean isCurrentUserMember = false;

    @Schema(description = "Whether the current user is the creator of this group", example = "false")
    private Boolean isCurrentUserCreator = false;

    @Schema(description = "Role of the current user in the group", example = "MEMBER")
    private String currentUserRole;
}

