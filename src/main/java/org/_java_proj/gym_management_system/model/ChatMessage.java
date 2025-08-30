package org._java_proj.gym_management_system.model;

import jakarta.persistence.*;
import lombok.*;
import org._java_proj.gym_management_system.common.constant.MessageType;
import org._java_proj.gym_management_system.common.entity.MasterData;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage extends MasterData {

    @Column(nullable = false)
    private Long senderId;

    private Long recipientId; // For private messages

    private Long studyGroupId; // For group messages

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MessageType messageType = MessageType.TEXT;

    private String attachmentUrl;

    @Builder.Default
    private Boolean isRead = false;

    @Builder.Default
    private Boolean isEdited = false;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", insertable = false, updatable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", insertable = false, updatable = false)
    private User recipient;
}
