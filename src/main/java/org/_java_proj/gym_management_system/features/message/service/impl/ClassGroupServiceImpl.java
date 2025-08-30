package org._java_proj.gym_management_system.features.message.service.impl;

import lombok.RequiredArgsConstructor;
import org._java_proj.gym_management_system.features.message.dto.request.ClassGroupCreateRequest;
import org._java_proj.gym_management_system.features.message.dto.response.ClassGroupResponse;
import org._java_proj.gym_management_system.features.message.dto.response.GroupMemberResponse;
import org._java_proj.gym_management_system.features.message.repository.ClassGroupRepository;
import org._java_proj.gym_management_system.features.message.repository.GroupMemberRepository;
import org._java_proj.gym_management_system.features.message.service.ClassGroupService;
import org._java_proj.gym_management_system.features.users.repository.UserRepository;
import org._java_proj.gym_management_system.model.ClassGroup;
import org._java_proj.gym_management_system.model.GroupMember;
import org._java_proj.gym_management_system.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClassGroupServiceImpl implements ClassGroupService {
    private final ClassGroupRepository classGroupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;

    @Transactional
    public ClassGroupResponse createClassGroup(ClassGroupCreateRequest request, Long creatorId) {
        userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ClassGroup studyGroup = ClassGroup.builder()
                .name(request.getName())
                .maxMembers(request.getMaxMembers())
                .currentMembers(1)
                .creatorId(creatorId)
                .isPrivate(request.getIsPrivate())
                .meetingSchedule(request.getMeetingSchedule())
                .groupImageUrl(request.getGroupImageUrl())
                .build();

        ClassGroup savedGroup = classGroupRepository.save(studyGroup);

        // Add creator as admin member
        GroupMember creatorMember = GroupMember.builder()
                .groupId(savedGroup.getId())
                .userId(creatorId)
                .role("admin")
                .isActive(true)
                .build();

        groupMemberRepository.save(creatorMember);

        return convertToclassGroupResponse(savedGroup, creatorId);
    }

    public List<ClassGroupResponse> getAllPublicGroups(Long userId) {
        Pageable pageable = PageRequest.of(0, 50);
        Page<ClassGroup> groups = classGroupRepository.findByIsPrivateFalseOrderByCreatedAtDesc(pageable);

        return groups.getContent().stream()
                .map(group -> convertToclassGroupResponse(group, userId))
                .collect(Collectors.toList());
    }

    public List<ClassGroupResponse> getUserGroups(Long userId) {
        List<GroupMember> memberships = groupMemberRepository.findByUserIdAndIsActiveTrue(userId);

        return memberships.stream()
                .map(membership -> {
                    ClassGroup group = classGroupRepository.findById(membership.getGroupId())
                            .orElse(null);
                    return group != null ? convertToclassGroupResponse(group, userId) : null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<ClassGroupResponse> searchGroups(String searchTerm, Long userId) {
        List<ClassGroup> groups = classGroupRepository.searchGroups(searchTerm);

        return groups.stream()
                .map(group -> convertToclassGroupResponse(group, userId))
                .collect(Collectors.toList());
    }

    public ClassGroupResponse getGroupById(Long groupId, Long userId) {
        ClassGroup group = classGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Study group not found"));

        return convertToclassGroupResponse(group, userId);
    }

    @Transactional
    public ClassGroupResponse joinGroup(Long groupId, Long userId) {
        ClassGroup group = classGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Study group not found"));

        // Check if user is already a member
        Optional<GroupMember> existingMember = groupMemberRepository
                .findByGroupIdAndUserIdAndIsActiveTrue(groupId, userId);

        if (existingMember.isPresent()) {
            throw new RuntimeException("User is already a member of this group");
        }

        // Check if group is full
        if (group.getCurrentMembers() >= group.getMaxMembers()) {
            throw new RuntimeException("Group is full");
        }

        // Add user as member
        GroupMember newMember = GroupMember.builder()
                .groupId(groupId)
                .userId(userId)
                .role("member")
                .isActive(true)
                .build();

        groupMemberRepository.save(newMember);

        // Update current members count
        group.setCurrentMembers(group.getCurrentMembers() + 1);
        ClassGroup updatedGroup = classGroupRepository.save(group);

        return convertToclassGroupResponse(updatedGroup, userId);
    }

    @Transactional
    public ClassGroupResponse leaveGroup(Long groupId, Long userId) {
        ClassGroup group = classGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Study group not found"));

        GroupMember member = groupMemberRepository
                .findByGroupIdAndUserIdAndIsActiveTrue(groupId, userId)
                .orElseThrow(() -> new RuntimeException("User is not a member of this group"));

        // Don't allow creator to leave if there are other members
        if (group.getCreatorId().equals(userId) && group.getCurrentMembers() > 1) {
            throw new RuntimeException("Creator cannot leave group with other members. Transfer ownership or delete group.");
        }

        // Remove member
        member.setIsActive(false);
        groupMemberRepository.save(member);

        // Update current members count
        group.setCurrentMembers(group.getCurrentMembers() - 1);
        ClassGroup updatedGroup = classGroupRepository.save(group);

        return convertToclassGroupResponse(updatedGroup, userId);
    }

    @Transactional
    public void deleteGroup(Long groupId, Long userId) {
        ClassGroup group = classGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Study group not found"));

        // Only creator can delete group
        if (!group.getCreatorId().equals(userId)) {
            throw new RuntimeException("Only group creator can delete the group");
        }

        classGroupRepository.delete(group);
    }

    public List<GroupMemberResponse> getGroupMembers(Long groupId, Long userId) {
        ClassGroup group = classGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Study group not found"));

        // Check if user has access
        Optional<GroupMember> userMembership = groupMemberRepository
                .findByGroupIdAndUserIdAndIsActiveTrue(groupId, userId);

        if (group.getIsPrivate() && userMembership.isEmpty()) {
            throw new RuntimeException("Access denied: Private group");
        }

        List<GroupMember> members = groupMemberRepository.findByGroupIdAndIsActiveTrue(groupId);

        return members.stream()
                .map(member
                        -> new GroupMemberResponse(
                        member.getId(),
                        member.getUserId(),
                        member.getUser() != null ? member.getUser().getProfile().getName() : null,
                        member.getRole(),
                        member.getJoinedAt()
                ))
                .toList();
    }


    private ClassGroupResponse convertToclassGroupResponse(ClassGroup group, Long currentUserId) {
        User creator = userRepository.findById(group.getCreatorId()).orElse(null);

        Optional<GroupMember> userMembership = currentUserId != null ?
                groupMemberRepository.findByGroupIdAndUserIdAndIsActiveTrue(group.getId(), currentUserId) :
                Optional.empty();

        boolean isMember = userMembership.isPresent();
        boolean isCreator = group.getCreatorId().equals(currentUserId);
        String userRole = userMembership.map(GroupMember::getRole).orElse(null);

        return ClassGroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .maxMembers(group.getMaxMembers())
                .currentMembers(group.getCurrentMembers())
                .isPrivate(group.getIsPrivate())
                .meetingSchedule(group.getMeetingSchedule())
                .groupImageUrl(group.getGroupImageUrl())
                .createdAt(group.getCreatedAt())
                .creatorName(creator != null ? creator.getProfile().getName() : "Unknown")
                .creatorEmail(creator != null ? creator.getEmail() : "unknown")
                .isCurrentUserMember(isMember)
                .isCurrentUserCreator(isCreator)
                .currentUserRole(userRole)
                .build();
    }
}
