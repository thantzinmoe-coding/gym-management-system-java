package org._java_proj.gym_management_system.features.managePackage.repository;

import org._java_proj.gym_management_system.common.constant.Status;
import org._java_proj.gym_management_system.model.GymPackage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GymPackageRepository extends JpaRepository<GymPackage, Long> {

    Page<GymPackage> findAllGymPackages(Pageable pageable);

}
