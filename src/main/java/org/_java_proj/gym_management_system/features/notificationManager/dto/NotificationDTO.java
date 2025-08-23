package org._java_proj.gym_management_system.features.notificationManager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@NoArgsConstructor
@AllArgsConstructor
@With
@Schema(description = "Notification request payload")
public class NotificationDTO {

    @Schema(description = "Title of the notification", example = "System Update")
    private String title;

    @Schema(description = "Content of the notification", example = "Server will restart at midnight.")
    private String content;

    @Schema(description = "Recipient user ID (omit for broadcast)", nullable = true, example = "5")
    private Long recipientId;

    @Schema(hidden = true) // don’t show in Swagger because backend sets it
    private Long senderId;

    @Schema(hidden = true)
    private String time;
}

