package org._java_proj.gym_management_system.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org._java_proj.gym_management_system.common.entity.MasterData;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Salary extends MasterData {

    @Column(nullable = false)
    private LocalDate paymentDate;

    @Column(nullable = false)
    private double amount;

    private String notes;

    @Column(nullable = false)
    private int salaryMonth;  // e.g., 9 for September

    @Column(nullable = false)
    private int salaryYear;   // e.g., 2025

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trainer_id", nullable = false)
    private User trainer;

    public Salary() {}
}