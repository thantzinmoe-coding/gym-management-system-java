package org._java_proj.gym_management_system.features.userDetailInfo.repository;

import org._java_proj.gym_management_system.common.constant.Status;
import org._java_proj.gym_management_system.model.UserDetailInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserDetailInfoRepository extends JpaRepository<UserDetailInfo, Long> {

    @Query("SELECT b FROM UserDetailInfo b WHERE b.entityId = :customerId")
    Page<UserDetailInfo> findByEntityId(@Param("customerId") Long customerId, Pageable pageable);

    Optional<UserDetailInfo> findByIdAndStatus(Long id, Status status);

    Optional<Object> findFirstByEntityIdAndStatus(Long entityId, Status status);

    boolean existsByEntityIdAndStatus(Long entityId, Status status);
}
