package org._java_proj.gym_management_system.features.manageAttendance.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org._java_proj.gym_management_system.model.Attendance;
import org._java_proj.gym_management_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByUser(User user);

    @Query("SELECT COALESCE(SUM(a.hoursWorked), 0) " +
            "FROM Attendance a " +
            "WHERE a.user.id = :userId")
    Double getTotalHoursWorkedByUser(@Param("userId") Long userId);
}
