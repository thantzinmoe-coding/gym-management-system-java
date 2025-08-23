package org._java_proj.gym_management_system.features.notificationManager.repository;

import org._java_proj.gym_management_system.model.UserNotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserNotificationStatusRepository extends JpaRepository<UserNotificationStatus, Long> {

    @Query("SELECT uns FROM UserNotificationStatus uns " +
            "JOIN FETCH uns.notification n " +
            "JOIN FETCH n.sender s " +
            "WHERE uns.user.id = :userId " +
            "ORDER BY n.time DESC")
    List<UserNotificationStatus> findAllByUserIdOrderByNotificationTimeDesc(Long userId);

    Optional<UserNotificationStatus> findByUserIdAndNotificationId(Long userId, Long notificationId);
}

