package org._java_proj.gym_management_system.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org._java_proj.gym_management_system.common.entity.MasterData;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Profile extends MasterData {

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String nrc;

    @Column(unique = true)
    private String phone;

    private LocalDate dob;

    private String gender;

    @Column(name = "profile_pic")
    private String profilePic;

    @Lob
    private String address;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    public Profile() {}

}
