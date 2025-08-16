package org._java_proj.gym_management_system.features.manageSchedule.repository;

import org._java_proj.gym_management_system.model.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query("SELECT s FROM Schedule s")
    Page<Schedule> findAllSchedules(Pageable pageable);
}
