package org.java_proj.gym_management_system.features.salary.service.impl;

import lombok.RequiredArgsConstructor;
import org.java_proj.gym_management_system.common.constant.Status;
import org.java_proj.gym_management_system.features.salary.dto.request.SalaryRequestDto;
import org.java_proj.gym_management_system.features.salary.dto.response.SalaryResponseDto;
import org.java_proj.gym_management_system.model.Salary;
import org.java_proj.gym_management_system.model.User;
import org.java_proj.gym_management_system.features.salary.repository.SalaryRepository;
import org.java_proj.gym_management_system.features.users.repository.UserRepository;

import org.java_proj.gym_management_system.features.salary.service.SalaryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalaryServiceImpl implements SalaryService {

    private final SalaryRepository salaryRepository;
    private final UserRepository userRepository;

    @Override
    public SalaryResponseDto createSalary(SalaryRequestDto salaryRequestDto) {
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();
        int year = now.getYear();

        boolean alreadyPaid = salaryRepository
                .findByTrainerIdOrderBySalaryYearDescSalaryMonthDesc(salaryRequestDto.getTrainerId())
                .stream()
                .anyMatch(s -> s.getSalaryMonth() == month && s.getSalaryYear() == year);

        if (alreadyPaid) {
            throw new IllegalStateException("Salary already paid for this trainer in " + month + "/" + year);
        }

        User trainer = userRepository.findById(salaryRequestDto.getTrainerId())
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found"));

        Salary salary = new Salary();
        salary.setPaymentDate(now);
        salary.setSalaryMonth(month);
        salary.setSalaryYear(year);
        salary.setAmount(salaryRequestDto.getAmount());
        salary.setNotes(salaryRequestDto.getNotes());
        salary.setStatus(Status.PAID);
        salary.setTrainer(trainer);

        Salary saved = salaryRepository.save(salary);
        return mapToResponseDto(saved);
    }

    @Transactional(readOnly = true)
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

    @jakarta.transaction.Transactional
    @Override
    public SalaryResponseDto updateSalary(Long id, SalaryRequestDto salaryRequestDto) {
        Salary existingSalary = salaryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Salary record not found with id: " + id));

        // The trainerId should not change during a payment update, so we can ignore it from the request.

        existingSalary.setAmount(salaryRequestDto.getAmount());
        existingSalary.setNotes(salaryRequestDto.getNotes());
        existingSalary.setPaymentDate(LocalDate.now()); // Set payment date on confirmation
        existingSalary.setStatus(Status.PAID); // <-- Change status to PAID

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
        responseDto.setTrainerId(salary.getTrainer().getId());
        responseDto.setTrainerName(
                salary.getTrainer().getProfile() != null
                        ? salary.getTrainer().getProfile().getName()
                        : "Unknown"
        );
        responseDto.setTrainerEmail(salary.getTrainer().getEmail());
        assert salary.getTrainer().getProfile() != null;
        responseDto.setTrainerAvatarUrl(salary.getTrainer().getProfile().getProfilePic());
        responseDto.setStatus(salary.getStatus());
        responseDto.setSalaryMonth(salary.getSalaryMonth());
        responseDto.setSalaryYear(salary.getSalaryYear());
        return responseDto;
    }

}