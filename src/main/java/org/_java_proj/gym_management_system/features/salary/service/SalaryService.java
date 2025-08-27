package org._java_proj.gym_management_system.features.salary.service;

import org._java_proj.gym_management_system.features.salary.dto.request.SalaryRequestDto;
import org._java_proj.gym_management_system.features.salary.dto.response.SalaryResponseDto;

import java.util.List;
import java.util.Optional;

public interface SalaryService {
    SalaryResponseDto createSalary(SalaryRequestDto salaryRequestDto);
    List<SalaryResponseDto> getAllSalaries();
    Optional<SalaryResponseDto> getSalaryById(Long id);
    SalaryResponseDto updateSalary(Long id, SalaryRequestDto salaryRequestDto);
    void deleteSalary(Long id);
}
