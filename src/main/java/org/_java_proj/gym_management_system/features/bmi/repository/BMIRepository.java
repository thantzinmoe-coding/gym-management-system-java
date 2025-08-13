package org._java_proj.gym_management_system.features.bmi.repository;

import org._java_proj.gym_management_system.common.constant.Status;
import org._java_proj.gym_management_system.model.BMI;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BMIRepository extends JpaRepository<BMI, Long> {

    @Query("SELECT b FROM BMI b WHERE b.entityId = :customerId")
    Page<BMI> findByEntityId(@Param("customerId") Long customerId, Pageable pageable);

    Optional<BMI> findFirstByEntityId(Long entityId);

    Optional<BMI> findByIdAndStatus(Long id, Status status);
}
