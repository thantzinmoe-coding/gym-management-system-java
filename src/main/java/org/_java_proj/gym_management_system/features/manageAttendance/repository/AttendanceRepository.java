package org._java_proj.gym_management_system.features.manageAttendance.repository;

import org._java_proj.gym_management_system.model.Attendance;
import org._java_proj.gym_management_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByUser(User user);
    Optional<Attendance> findByUserAndDate(User user, LocalDate date);
}
