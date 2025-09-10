package org._java_proj.gym_management_system.features.salary.service;

import lombok.RequiredArgsConstructor;
import org._java_proj.gym_management_system.common.constant.Status;
import org._java_proj.gym_management_system.model.Salary;
import org._java_proj.gym_management_system.model.User;
import org._java_proj.gym_management_system.features.salary.repository.SalaryRepository;
import org._java_proj.gym_management_system.features.users.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalaryScheduler {

    private final UserRepository userRepository;
    private final SalaryRepository salaryRepository;

    /**
     * Runs at 00:00 on the 1st day of every month.
     * Creates a pending salary record for all active trainers.
     */
    @Scheduled(cron = "0 0 0 1 * *")
//    @Scheduled(cron = "*/10 * * * * *")
    public void createPendingSalariesForNewMonth() {
        LocalDate now = LocalDate.now();
        int month = 10;
        int year = now.getYear();

        List<User> activeTrainers = new ArrayList<>();
        Pageable pageable = PageRequest.of(0, 100); // Process 100 trainers at a time

        Page<User> trainerPage;
        do {
            // Fetch a page of trainers
            trainerPage = userRepository.findByRoleAndStatus("TRAINER", Status.ACTIVE, pageable);

            // Add the trainers from the current page to our list
            activeTrainers.addAll(trainerPage.getContent());

            // Get the request for the next page
            pageable = trainerPage.nextPageable();

        } while (trainerPage.hasNext());

        for (User trainer : activeTrainers) {
            // Check if a salary record for this month already exists to avoid duplicates
            boolean recordExists = salaryRepository
                    .findByTrainerIdAndSalaryYearAndSalaryMonth(trainer.getId(), year, month)
                    .isPresent();

            if (!recordExists) {
                Salary pendingSalary = new Salary();
                pendingSalary.setTrainer(trainer);
                pendingSalary.setAmount(0.0); // Initial amount is 0
                pendingSalary.setPaymentDate(LocalDate.now());
                pendingSalary.setSalaryMonth(month);
                pendingSalary.setSalaryYear(year);
                pendingSalary.setStatus(Status.PENDING); // Set status to PENDING
                pendingSalary.setNotes("Pending payment for " + now.getMonth().name() + " " + year);

                salaryRepository.save(pendingSalary);
                System.out.println("Created PENDING salary for " + trainer.getProfile().getName());
            }
        }
    }
}
