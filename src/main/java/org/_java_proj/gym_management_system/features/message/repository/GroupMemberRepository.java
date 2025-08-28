package org._java_proj.gym_management_system.features.message.repository;

import org._java_proj.gym_management_system.model.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    List<GroupMember> findByUserIdAndIsActiveTrue(Long userId);

    Optional<GroupMember> findByGroupIdAndUserIdAndIsActiveTrue(Long id, Long currentUserId);

    List<GroupMember> findByGroupIdAndIsActiveTrue(Long groupId);

    @Query("SELECT CASE WHEN COUNT(gm) > 0 THEN true ELSE false END FROM GroupMember gm " +
            "WHERE gm.userId = :userId AND gm.groupId = :groupId AND gm.isActive = true")
    boolean existsByUserIdAndGroupId(Long senderId, Long studyGroupId);

    @Query("SELECT gm.userId FROM GroupMember gm WHERE gm.groupId = :groupId AND gm.isActive = true")
    List<Long> findUserIdsByGroupId(@Param("groupId") Long groupId);
}
