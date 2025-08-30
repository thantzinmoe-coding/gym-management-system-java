package org._java_proj.gym_management_system.features.users.repository;

import org._java_proj.gym_management_system.features.profile.dto.request.ProfileProjection;
import org._java_proj.gym_management_system.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUser_Id(Long userId);

    @Query("SELECT p.name as name, p.phone as phone FROM Profile p WHERE p.user.id = :userId")
    Optional<ProfileProjection> findProfileSummaryByUserId(@Param("userId") Long userId);

}
