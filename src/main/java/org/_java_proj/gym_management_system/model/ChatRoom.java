package org._java_proj.gym_management_system.model;

import jakarta.persistence.*;
import lombok.*;
import org._java_proj.gym_management_system.common.entity.MasterData;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom extends MasterData {

    @Column(nullable = false)
    private Long user1Id;

    @Column(nullable = false)
    private Long user2Id;

    private Long lastMessageId;

    private LocalDateTime lastMessageAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1_id", insertable = false, updatable = false)
    private User user1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2_id", insertable = false, updatable = false)
    private User user2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_message_id", insertable = false, updatable = false)
    private ChatMessage lastMessage;
}
