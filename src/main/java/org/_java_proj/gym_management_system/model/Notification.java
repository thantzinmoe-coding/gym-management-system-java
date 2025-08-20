package org._java_proj.gym_management_system.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import org._java_proj.gym_management_system.common.entity.MasterData;

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


    @ManyToOne
    private User recipient;

    @ManyToOne
    private User sender;

    // getters and setters
}
