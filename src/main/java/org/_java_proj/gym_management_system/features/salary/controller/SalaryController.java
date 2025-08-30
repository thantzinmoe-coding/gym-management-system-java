package org._java_proj.gym_management_system.features.salary.controller;


import org._java_proj.gym_management_system.features.salary.dto.request.SalaryRequestDto;
import org._java_proj.gym_management_system.features.salary.dto.response.SalaryResponseDto;
import org._java_proj.gym_management_system.features.salary.service.SalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("${api.base.path}/salaries")
public class SalaryController {

    @Autowired
    private SalaryService salaryService;

    @PostMapping
    public ResponseEntity<SalaryResponseDto> createSalary(@RequestBody SalaryRequestDto salaryRequestDto) {
        SalaryResponseDto createdSalary = salaryService.createSalary(salaryRequestDto);
        return new ResponseEntity<>(createdSalary, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<SalaryResponseDto>> getAllSalaries() {
        List<SalaryResponseDto> salaries = salaryService.getAllSalaries();
        return new ResponseEntity<>(salaries, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalaryResponseDto> getSalaryById(@PathVariable Long id) {
        Optional<SalaryResponseDto> salary = salaryService.getSalaryById(id);
        return salary.map(responseDto -> new ResponseEntity<>(responseDto, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SalaryResponseDto> updateSalary(@PathVariable Long id, @RequestBody SalaryRequestDto salaryRequestDto) {
        try {
            SalaryResponseDto updatedSalary = salaryService.updateSalary(id, salaryRequestDto);
            return new ResponseEntity<>(updatedSalary, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Or HttpStatus.BAD_REQUEST depending on the error
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSalary(@PathVariable Long id) {
        salaryService.deleteSalary(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}