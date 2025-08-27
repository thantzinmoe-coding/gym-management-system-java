package org._java_proj.gym_management_system.features.salary.repository;


import org._java_proj.gym_management_system.model.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, Long> {
    // Optional: Add custom query methods here
}
