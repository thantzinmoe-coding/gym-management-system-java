package org._java_proj.gym_management_system.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org._java_proj.gym_management_system.common.entity.MasterData;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Feedback extends MasterData {

    @Column(nullable = false)
    private LocalDate date;

    private String comment;

    @Column(nullable = false)
    private Integer ratingPoints;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id", nullable = false)
    private User trainer;

    public Feedback() {}

    public Feedback(final LocalDate date, final String comment, final Integer ratingPoints, final User user, final User trainer) {
        this.date = date;
        this.comment = comment;
        this.ratingPoints = ratingPoints;
        this.user = user;
        this.trainer = trainer;
    }
}
