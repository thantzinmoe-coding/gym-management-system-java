package org._java_proj.gym_management_system.features.bookPackage.repository;

import org._java_proj.gym_management_system.model.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookPackageRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b")
    Page<Booking> getAllBookings(Pageable pageable);
}
