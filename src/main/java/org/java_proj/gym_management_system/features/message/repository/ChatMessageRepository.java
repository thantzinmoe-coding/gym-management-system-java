package org.java_proj.gym_management_system.features.message.repository;

import org.java_proj.gym_management_system.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("SELECT m FROM ChatMessage m WHERE " +
            "(m.senderId = :userId1 AND m.recipientId = :userId2) OR " +
            "(m.senderId = :userId2 AND m.recipientId = :userId1) ORDER BY m.createdAt ASC")
    List<ChatMessage> findPrivateMessages(Long userId1, Long userId2);

}