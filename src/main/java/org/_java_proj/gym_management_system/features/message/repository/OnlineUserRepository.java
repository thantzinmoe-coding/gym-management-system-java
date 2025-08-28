package org._java_proj.gym_management_system.features.message.repository;

import org._java_proj.gym_management_system.model.OnlineUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OnlineUserRepository extends JpaRepository<OnlineUser, Long> {
    Optional<OnlineUser> findByUserId(Long userId);

    Optional<OnlineUser> findByUserIdAndSessionId(Long userId, String sessionId);

    @Query("SELECT ou FROM OnlineUser ou WHERE ou.userId IN :userIds AND ou.isActive = true")
    List<OnlineUser> findActiveUsersByIdIn(List<Long> userIds);

    @Modifying
    @Query("UPDATE OnlineUser ou SET ou.isActive = false WHERE ou.lastSeen < :cutoffTime")
    void deactivateStaleUsers(LocalDateTime cutoff);
}
