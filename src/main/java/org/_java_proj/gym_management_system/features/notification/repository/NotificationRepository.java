package org._java_proj.gym_management_system.features.notification.repository;

import org._java_proj.gym_management_system.model.Notification;
import org._java_proj.gym_management_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipient(User recipient);
}
