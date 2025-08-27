package org._java_proj.gym_management_system.features.salary.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SalaryRequestDto {
    private LocalDate paymentDate;
    private double amount;
    private String notes;
    private double deduction;
    private Long trainerId; // Use trainerId in request
}
