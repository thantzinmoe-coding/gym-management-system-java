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

    @Column(name = "time_in", nullable = false)
    private LocalDateTime timeIn;

    @Column(name = "time_out")
    private LocalDateTime timeOut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "attendance_type", nullable = false)
    private AttendanceType attendanceType;

    // For trainers
    @Column(name = "hours_worked")
    private Double hoursWorked;

    // For users
    @Column(name = "package_days")
    private Integer packageDays;
}
