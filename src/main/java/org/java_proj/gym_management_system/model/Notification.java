package org.java_proj.gym_management_system.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import org.java_proj.gym_management_system.common.entity.MasterData;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Notification extends MasterData {
    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime time;

    private String recipientRole;

    @ManyToOne
    private User recipient;

    @ManyToOne
    @JsonBackReference
    private User sender;

    private boolean isRead = false;

    // getters and setters
}
