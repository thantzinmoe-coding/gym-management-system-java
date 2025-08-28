package org._java_proj.gym_management_system.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org._java_proj.gym_management_system.common.entity.MasterData;
import org._java_proj.gym_management_system.common.constant.AttendanceType; // Corrected import

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Attendance extends MasterData {

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String timeIn;

    private String timeOut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "attendance_type", nullable = false)
    private AttendanceType attendanceType;

    // For trainers
    private Double hoursWorked;

    // For users
    private Integer packageDays;

    public Attendance(){}
}
