package org._java_proj.gym_management_system.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org._java_proj.gym_management_system.common.entity.MasterData;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class User extends MasterData {

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Profile profile;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Booking booking;

    @OneToOne(mappedBy = "trainer", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Salary salary;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Feedback> givenFeedback = new HashSet<>();

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Feedback> receivedFeedback = new HashSet<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Attendance attendance;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private BMI bmi;

    @OneToOne(mappedBy = "sender", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Chat sender;

    @OneToOne(mappedBy = "receiver", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Chat receiver;

    public User() {}

    public void giveRating(final Feedback feedback) {
        this.givenFeedback.add(feedback);
        feedback.setTrainer(this);
    }

    public void removeGivenRating(final Feedback feedback) {
        this.givenFeedback.remove(feedback);
        feedback.setTrainer(null);
    }

    public void receiveRating(final Feedback feedback) {
        this.receivedFeedback.add(feedback);
        feedback.setTrainer(this);
    }

    public void removeReceivedRating(final Feedback feedback) {
        this.receivedFeedback.remove(feedback);
        feedback.setTrainer(null);
    }
}
