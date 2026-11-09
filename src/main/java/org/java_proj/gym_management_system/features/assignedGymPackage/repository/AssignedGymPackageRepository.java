package org.java_proj.gym_management_system.features.assignedGymPackage.repository;

import org.java_proj.gym_management_system.common.constant.Status;
import org.java_proj.gym_management_system.model.AssignedGymPackage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssignedGymPackageRepository extends JpaRepository<AssignedGymPackage, Long> {

    AssignedGymPackage findByTrainerIdAndStatus(Long trainerID, Status status);

    int countByTrainerIdAndStatus(Long trainerId, Status status);

    boolean existsByGymPackageIdAndStatus(Long gymPackageId, Status status);

    Optional<AssignedGymPackage> findByTrainerIdAndGymPackageIdAndStatus(Long trainerID, Long packageId, Status status);

    Optional<AssignedGymPackage> findByGymPackageIdAndStatus(Long packageId, Status status);

    Page<AssignedGymPackage> findByTrainerId(Long trainerId, Pageable pageable);
}
