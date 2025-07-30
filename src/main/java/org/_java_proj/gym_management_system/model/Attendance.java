package org._java_proj.gym_management_system.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org._java_proj.gym_management_system.common.entity.MasterData;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Attendance extends MasterData {

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String timeIn;

    @Column(nullable = false)
    private String timeOut;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    public Attendance() {}
}
