package org._java_proj.gym_management_system.features.notification.repository;

import org._java_proj.gym_management_system.features.notification.dto.UserNotificationDTO;
import org._java_proj.gym_management_system.model.UserNotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserNotificationStatusRepository extends JpaRepository<UserNotificationStatus, Long> {

    Optional<UserNotificationStatus> findByUserIdAndNotificationId(Long userId, Long notificationId);

    List<UserNotificationStatus> findAllByUserId(Long userId);

    @Query("""
    SELECT new org._java_proj.gym_management_system.features.notification.dto.UserNotificationDTO(
        uns.notification.id,
        uns.notification.title,
        uns.notification.content,
        uns.notification.time,
        uns.isRead
    )
    FROM UserNotificationStatus uns
    JOIN uns.notification n
    WHERE uns.user.id = :userId
    ORDER BY n.time DESC
    """)
    List<UserNotificationDTO> findUserNotifications(@Param("userId") Long userId);

}

