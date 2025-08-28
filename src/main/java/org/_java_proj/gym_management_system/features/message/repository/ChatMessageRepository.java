package org._java_proj.gym_management_system.features.message.repository;

import org._java_proj.gym_management_system.model.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    @Query("SELECT cm FROM ChatMessage cm WHERE " +
            "((cm.senderId = :user1Id AND cm.recipientId = :user2Id) OR " +
            "(cm.senderId = :user2Id AND cm.recipientId = :user1Id)) " +
            "AND cm.studyGroupId IS NULL " +
            "ORDER BY cm.createdAt DESC")
    Page<ChatMessage> findPrivateMessagesBetweenUsers(
            @Param("user1Id") Long user1Id,
            @Param("user2Id") Long user2Id,
            Pageable pageable
    );

    // Group chat messages
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.studyGroupId = :studyGroupId " +
            "ORDER BY cm.createdAt DESC")
    Page<ChatMessage> findGroupMessages(@Param("studyGroupId") Long studyGroupId, Pageable pageable);

    // Unread private messages count
    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE " +
            "cm.recipientId = :userId AND cm.isRead = false AND cm.studyGroupId IS NULL")
    Long countUnreadPrivateMessages(@Param("userId") Long userId);

    // Unread group messages count for specific group
    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE " +
            "cm.studyGroupId = :studyGroupId AND cm.senderId != :userId AND cm.isRead = false")
    Long countUnreadGroupMessages(@Param("studyGroupId") Long studyGroupId, @Param("userId") Long userId);

    // Mark messages as read
    @Modifying
    @Query("UPDATE ChatMessage cm SET cm.isRead = true WHERE " +
            "cm.recipientId = :userId AND cm.senderId = :senderId AND cm.isRead = false")
    void markPrivateMessagesAsRead(@Param("userId") Long userId, @Param("senderId") Long senderId);

    @Modifying
    @Query("UPDATE ChatMessage cm SET cm.isRead = true WHERE " +
            "cm.studyGroupId = :studyGroupId AND cm.senderId != :userId AND cm.isRead = false")
    void markGroupMessagesAsRead(@Param("studyGroupId") Long studyGroupId, @Param("userId") Long userId);

    // Recent messages for chat list
    @Query("SELECT cm FROM ChatMessage cm WHERE " +
            "(cm.senderId = :userId OR cm.recipientId = :userId) AND cm.studyGroupId IS NULL " +
            "AND cm.createdAt = (SELECT MAX(cm2.createdAt) FROM ChatMessage cm2 WHERE " +
            "((cm2.senderId = cm.senderId AND cm2.recipientId = cm.recipientId) OR " +
            "(cm2.senderId = cm.recipientId AND cm2.recipientId = cm.senderId)) AND cm2.studyGroupId IS NULL)")
    List<ChatMessage> findRecentPrivateMessages(@Param("userId") Long userId);

    // Last message in group
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.studyGroupId = :studyGroupId " +
            "ORDER BY cm.createdAt DESC")
    List<ChatMessage> findLastGroupMessage(@Param("studyGroupId") Long studyGroupId, Pageable pageable);
}
