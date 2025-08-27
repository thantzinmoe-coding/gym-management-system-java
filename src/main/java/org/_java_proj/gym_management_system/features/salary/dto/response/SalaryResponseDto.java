package org._java_proj.gym_management_system.features.salary.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SalaryResponseDto {
    private Long id;
    private LocalDate paymentDate;
    private double amount;
    private String notes;
    private double deduction;
    private Long trainerId;
}
