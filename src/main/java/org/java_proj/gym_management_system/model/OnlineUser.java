package org.java_proj.gym_management_system.model;

import jakarta.persistence.*;
import lombok.*;
import org.java_proj.gym_management_system.common.entity.MasterData;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnlineUser extends MasterData {
    @Column(nullable = false, unique = true)
    private Long userId;

    @Column( nullable = false)
    private String sessionId;

    @Column(nullable = false)
    private LocalDateTime lastSeen;

    @Builder.Default
    private Boolean isActive = true;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
}
