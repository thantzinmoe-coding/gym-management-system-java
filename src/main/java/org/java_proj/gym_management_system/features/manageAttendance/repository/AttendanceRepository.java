package org.java_proj.gym_management_system.features.manageAttendance.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.java_proj.gym_management_system.model.Attendance;
import org.java_proj.gym_management_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByUser(User user);

    @Query("""
       SELECT COALESCE(SUM(a.hoursWorked), 0)
       FROM Attendance a
       WHERE a.user.id = :userId
         AND MONTH(a.date) = MONTH(CURRENT_DATE)
         AND YEAR(a.date) = YEAR(CURRENT_DATE)
       """)
    Double getTotalHoursWorkedByUserForCurrentMonth(@Param("userId") Long userId);

}
