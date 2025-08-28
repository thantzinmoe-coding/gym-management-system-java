package org._java_proj.gym_management_system.features.message.repository;

import org._java_proj.gym_management_system.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("SELECT cr FROM ChatRoom cr WHERE " +
            "(cr.user1Id = :user1Id AND cr.user2Id = :user2Id) OR " +
            "(cr.user1Id = :user2Id AND cr.user2Id = :user1Id)")
    Optional<ChatRoom> findByUsers(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);

    @Query("SELECT cr FROM ChatRoom cr WHERE " +
            "cr.user1Id = :userId OR cr.user2Id = :userId " +
            "ORDER BY cr.lastMessageAt DESC")
    List<ChatRoom> findByUserIdOrderByLastMessageAtDesc(@Param("userId") Long userId);
}
