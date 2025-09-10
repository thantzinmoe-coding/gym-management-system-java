package org._java_proj.gym_management_system.model;

import jakarta.persistence.*;
import lombok.*;
import org._java_proj.gym_management_system.common.constant.MessageType;
import org._java_proj.gym_management_system.common.entity.MasterData;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage extends MasterData {

    private Long senderId;

    private Long recipientId; // Null for group messages

    private Long studyGroupId; // Null for private messages

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    private String attachmentUrl;

    private boolean isRead = false;

    private boolean isEdited = false;

    private LocalDateTime editedAt;

}