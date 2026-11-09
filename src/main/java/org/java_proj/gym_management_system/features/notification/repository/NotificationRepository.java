package org.java_proj.gym_management_system.features.notification.repository;

import org.java_proj.gym_management_system.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipientIsNullOrderByTimeDesc(); // For broadcast notifications
    List<Notification> findByRecipientIdOrderByTimeDesc(Long recipientId); // For specific user

}
