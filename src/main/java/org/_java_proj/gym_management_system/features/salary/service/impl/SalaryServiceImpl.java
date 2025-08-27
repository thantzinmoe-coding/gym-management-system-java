package org._java_proj.gym_management_system.features.salary.service.impl;
import org._java_proj.gym_management_system.features.salary.dto.request.SalaryRequestDto;
import org._java_proj.gym_management_system.features.salary.dto.response.SalaryResponseDto;
import org._java_proj.gym_management_system.model.Salary;
import org._java_proj.gym_management_system.model.User;
import org._java_proj.gym_management_system.features.salary.repository.SalaryRepository;
import org._java_proj.gym_management_system.features.users.repository.UserRepository;

import org._java_proj.gym_management_system.features.salary.service.SalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SalaryServiceImpl implements SalaryService {

    @Autowired
    private SalaryRepository salaryRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public SalaryResponseDto createSalary(SalaryRequestDto salaryRequestDto) {
        User trainer = userRepository.findById(salaryRequestDto.getTrainerId())
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found with id: " + salaryRequestDto.getTrainerId()));

        Salary salary = new Salary();
        salary.setPaymentDate(salaryRequestDto.getPaymentDate());
        salary.setAmount(salaryRequestDto.getAmount());
        salary.setNotes(salaryRequestDto.getNotes());
        salary.setDeduction(salaryRequestDto.getDeduction());
        salary.setTrainer(trainer);

        Salary savedSalary = salaryRepository.save(salary);
        return mapToResponseDto(savedSalary);
    }

    @Override
    public List<SalaryResponseDto> getAllSalaries() {
        List<Salary> salaries = salaryRepository.findAll();
        return salaries.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<SalaryResponseDto> getSalaryById(Long id) {
        Optional<Salary> salary = salaryRepository.findById(id);
        return salary.map(this::mapToResponseDto);
    }

    @Override
    public SalaryResponseDto updateSalary(Long id, SalaryRequestDto salaryRequestDto) {
        Salary existingSalary = salaryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Salary not found with id: " + id));

        User trainer = userRepository.findById(salaryRequestDto.getTrainerId())
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found with id: " + salaryRequestDto.getTrainerId()));

        existingSalary.setPaymentDate(salaryRequestDto.getPaymentDate());
        existingSalary.setAmount(salaryRequestDto.getAmount());
        existingSalary.setNotes(salaryRequestDto.getNotes());
        existingSalary.setDeduction(salaryRequestDto.getDeduction());
        existingSalary.setTrainer(trainer);

        Salary updatedSalary = salaryRepository.save(existingSalary);
        return mapToResponseDto(updatedSalary);
    }

    @Override
    public void deleteSalary(Long id) {
        salaryRepository.deleteById(id);
    }

    private SalaryResponseDto mapToResponseDto(Salary salary) {
        SalaryResponseDto responseDto = new SalaryResponseDto();
        responseDto.setId(salary.getId());
        responseDto.setPaymentDate(salary.getPaymentDate());
        responseDto.setAmount(salary.getAmount());
        responseDto.setNotes(salary.getNotes());
        responseDto.setDeduction(salary.getDeduction());
        responseDto.setTrainerId(salary.getTrainer().getId());
        return responseDto;
    }
}