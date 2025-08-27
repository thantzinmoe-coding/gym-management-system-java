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

    private double deduction;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trainer_id", nullable = false, unique = true)
    private User trainer;

    public Salary() {}
}