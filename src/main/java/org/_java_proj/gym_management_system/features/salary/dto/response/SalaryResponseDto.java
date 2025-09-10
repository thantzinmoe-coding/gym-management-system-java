package org._java_proj.gym_management_system.features.salary.dto.response;

import lombok.Getter;
import lombok.Setter;
import org._java_proj.gym_management_system.common.constant.Status;

import java.time.LocalDate;

@Getter
@Setter
public class SalaryResponseDto {
    private Long id;
    private LocalDate paymentDate;
    private double amount;
    private String notes;
    private Long trainerId;
    private String trainerName;
    private Status status;

    // Add these
    private int salaryMonth;
    private int salaryYear;
}

