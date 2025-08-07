package org._java_proj.gym_management_system.model;

import jakarta.persistence.*;
import lombok.Data;
import org._java_proj.gym_management_system.common.entity.MasterData; // Assuming this is a valid base class

import java.time.LocalDate;

@Entity
@Data // Lombok generates getters, setters, etc.
@Table
public class Feedback extends MasterData { // Inherits from MasterData

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false) // Assuming comment is required
    private String comment;


    @Column(nullable = false) // Assuming ratingPoints is required
    private Integer ratingPoints;

    // Relationships are present in your entity, which is good for a full system,
    // but for just testing the feedback submission POST, they aren't strictly needed
    // for the basic input mapping.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id", nullable = false)
    private User trainer;

    // No-args constructor
    public Feedback() {}

    // Constructor used in your latest provided Feedback.java
    public Feedback(final LocalDate date, final String comment, final Integer ratingPoints, final User user, final User trainer) {
        this.date = date;
        this.comment = comment;
        this.ratingPoints = ratingPoints;
        this.user = user;
        this.trainer = trainer;

    }
}
