package org.java_proj.gym_management_system.model;

import jakarta.persistence.*;
import lombok.*;
import org.java_proj.gym_management_system.common.entity.MasterData;

import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChatRoom extends MasterData {

    // A unique identifier for private chats, e.g., "user1_user2"
    private String roomIdentifier; // e.g., "user1_user2"
    private Long user1Id;
    private Long user2Id;

    private Long lastMessageId;
    private LocalDateTime lastMessageAt;
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}