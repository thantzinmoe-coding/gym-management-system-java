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

    /**
     * Admin list: newest first, one page at a time, optionally one record type and a name search.
     * User, role and profile come in the same query (no extra query per row).
     */
    @Query(value = """
            SELECT a FROM Attendance a
            JOIN FETCH a.user u
            JOIN FETCH u.role
            LEFT JOIN FETCH u.profile p
            WHERE a.attendanceType = :type AND a.date >= :from
              AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
            ORDER BY a.date DESC, a.id DESC
            """,
            countQuery = """
            SELECT COUNT(a) FROM Attendance a
            LEFT JOIN a.user u
            LEFT JOIN u.profile p
            WHERE a.attendanceType = :type AND a.date >= :from
              AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    org.springframework.data.domain.Page<Attendance> searchByType(
            @Param("type") org.java_proj.gym_management_system.common.constant.AttendanceType type,
            @Param("from") java.time.LocalDate from,
            @Param("keyword") String keyword,
            org.springframework.data.domain.Pageable pageable);

    /** Same as {@link #searchByType} for both record types together. */
    @Query(value = """
            SELECT a FROM Attendance a
            JOIN FETCH a.user u
            JOIN FETCH u.role
            LEFT JOIN FETCH u.profile p
            WHERE a.date >= :from
              AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
            ORDER BY a.date DESC, a.id DESC
            """,
            countQuery = """
            SELECT COUNT(a) FROM Attendance a
            LEFT JOIN a.user u
            LEFT JOIN u.profile p
            WHERE a.date >= :from
              AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    org.springframework.data.domain.Page<Attendance> searchAllTypes(
            @Param("from") java.time.LocalDate from,
            @Param("keyword") String keyword,
            org.springframework.data.domain.Pageable pageable);

    @Query("""
       SELECT COALESCE(SUM(a.hoursWorked), 0)
       FROM Attendance a
       WHERE a.user.id = :userId
         AND MONTH(a.date) = MONTH(CURRENT_DATE)
         AND YEAR(a.date) = YEAR(CURRENT_DATE)
       """)
    Double getTotalHoursWorkedByUserForCurrentMonth(@Param("userId") Long userId);

}
