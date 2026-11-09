package org.java_proj.gym_management_system.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.java_proj.gym_management_system.common.entity.MasterData;

import java.util.*;

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
    @JsonManagedReference
    private Profile profile;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private UserDetailInfo userDetailInfo;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Feedback> givenFeedback = new HashSet<>();

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Feedback> receivedFeedback = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Attendance> attendance;

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<AssignedGymPackage> assignedGymPackages = new ArrayList<>();

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Salary> salary = new ArrayList<>();

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference // The forward part of the reference
    private List<Notification> sentNotifications;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserNotificationStatus> notificationStatuses = new ArrayList<>();


    public User() {}

    public void giveRating(final Feedback feedback) {
        this.givenFeedback.add(feedback);
        feedback.setMember(this);
    }

    public void removeGivenRating(final Feedback feedback) {
        this.givenFeedback.remove(feedback);
        feedback.setMember(null);
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
