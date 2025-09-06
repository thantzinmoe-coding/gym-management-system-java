package org._java_proj.gym_management_system.model;

import jakarta.persistence.*;
import lombok.*;
import org._java_proj.gym_management_system.common.entity.MasterData;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom extends MasterData {

    // A unique identifier for private chats, e.g., "user1_user2"
    @Column(unique = true)
    private String roomIdentifier;

    private Long user1Id;

    private Long user2Id;

    private Long lastMessageId;

    @UpdateTimestamp
    private LocalDateTime lastMessageAt;
}