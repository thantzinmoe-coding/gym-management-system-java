package org.java_proj.gym_management_system.features.managePackage.repository;

import org.java_proj.gym_management_system.common.constant.GymPackageType;
import org.java_proj.gym_management_system.common.constant.Status;
import org.java_proj.gym_management_system.model.GymPackage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GymPackageRepository extends JpaRepository<GymPackage, Long> {

    @Query("SELECT g FROM GymPackage g")
    Page<GymPackage> findAllGymPackages(Pageable pageable);

    Optional<GymPackage> findByIdAndStatus(Long gymPackageId, Status status);

    Page<GymPackage> findByGymPackageType(GymPackageType type, Pageable pageable);

}
