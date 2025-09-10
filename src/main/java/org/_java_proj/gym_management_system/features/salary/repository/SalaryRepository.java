package org._java_proj.gym_management_system.features.salary.repository;


import org._java_proj.gym_management_system.model.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, Long> {
    List<Salary> findByTrainerIdOrderBySalaryYearDescSalaryMonthDesc(Long trainerId);

    List<Salary> findBySalaryYearAndSalaryMonth(int year, int month);

    Optional<Salary> findByTrainerIdAndSalaryYearAndSalaryMonth(Long id, int year, int month);
}

