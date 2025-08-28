package org._java_proj.gym_management_system.features.bookPackage.repository;

import jakarta.validation.constraints.NotBlank;
import org._java_proj.gym_management_system.common.constant.MemberStatus;
import org._java_proj.gym_management_system.features.superAdmin.dto.response.BookingDetailResponse;
import org._java_proj.gym_management_system.model.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookPackageRepository extends JpaRepository<Booking, Long> {

    // Existing methods
    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.user.id = :memberId AND b.memberStatus IN :activeStatuses")
    boolean hasActiveBooking(@Param("memberId") Long memberId, @Param("activeStatuses") List<MemberStatus> activeStatuses);

    @Query("SELECT b FROM Booking b WHERE b.user.id = :memberId AND b.memberStatus IN :activeStatuses")
    Optional<Booking> findActiveBookingByMemberId(@Param("memberId") Long memberId, @Param("activeStatuses") List<MemberStatus> activeStatuses);

    @Query("SELECT b FROM Booking b WHERE b.user.id = :memberId ORDER BY b.createdAt DESC")
    List<Booking> findBookingHistoryByMemberId(@Param("memberId") Long memberId);

    // New methods for the additional endpoints
    Page<Booking> findByMemberStatus(MemberStatus memberStatus, Pageable pageable);

    long countByMemberStatus(MemberStatus memberStatus);

    long countByMemberStatusIn(List<MemberStatus> memberStatuses);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.createdAt BETWEEN :startDate AND :endDate")
    long countBookingsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT gp.id, gp.name, COUNT(b) FROM Booking b " +
            "JOIN b.gymPackage gp " +
            "GROUP BY gp.id, gp.name " +
            "ORDER BY COUNT(b) DESC")
    List<Object[]> findTopBookedGymPackages(@Param("limit") int limit);

    boolean existsByUserIdAndMemberStatus(@NotBlank(message = "Entity ID is required") Long entityId, MemberStatus status);

    Page<Booking> findByUserId(Long memberId, Pageable pageable);

    Page<Booking> findByGymPackageId(Long packageId, Pageable pageable);

    Optional<Object> findByIdAndMemberStatus(Long bookingId, MemberStatus memberStatus);

    // In BookingRepository
    // In BookingRepository
    @Query("SELECT new org._java_proj.gym_management_system.features.superAdmin.dto.response.BookingDetailResponse(" +
            "b.id, " +
            "b.user.id, " +
            "p.name, " +
            "b.gymPackage.id, " +
            "g.name, " +
            "b.memberStatus) " +
            "FROM Booking b " +
            "LEFT JOIN User u ON u.id = b.user.id " +
            "LEFT JOIN Profile p ON p.user.id = u.id " +
            "LEFT JOIN GymPackage g ON g.id = b.gymPackage.id " +
            "WHERE (:status IS NULL OR b.memberStatus = :status ) " +
            "AND (:keyword IS NULL OR :keyword = '' OR " +
            "     LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "     LOWER(g.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:memberId IS NULL OR b.user.id = :memberId) " +
            "AND (:packageId IS NULL OR b.gymPackage.id = :packageId)")
    Page<BookingDetailResponse> findPendingBookingsWithDetails(
            @Param("status") MemberStatus status,
            @Param("keyword") String keyword,
            @Param("memberId") Long memberId,
            @Param("packageId") Long packageId,
            Pageable pageable
    );

    @Query("SELECT COUNT(DISTINCT b.user.id) " +
            "FROM Booking b " +
            "JOIN AssignedGymPackage agp ON agp.assignedGymPackage.id = b.gymPackage.id " +
            "WHERE agp.trainer.id = :trainerId")
    Long countDistinctUsersByTrainer(@Param("trainerId") Long trainerId);


}
