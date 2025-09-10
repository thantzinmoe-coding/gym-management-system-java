package org._java_proj.gym_management_system.features.message.service;

import org._java_proj.gym_management_system.features.message.dto.request.ClassGroupCreateRequest;
import org._java_proj.gym_management_system.features.message.dto.response.ClassGroupResponse;
import org._java_proj.gym_management_system.features.message.dto.response.GroupMemberResponse;

import java.util.List;

public interface ClassGroupService {
    ClassGroupResponse createClassGroup(ClassGroupCreateRequest request, Long creatorId);
    List<ClassGroupResponse> getAllPublicGroups(Long userId);
    List<ClassGroupResponse> getUserGroups(Long userId);
    List<ClassGroupResponse> searchGroups(String searchTerm, Long userId);
    ClassGroupResponse getGroupById(Long groupId, Long userId);
    ClassGroupResponse joinGroup(Long groupId, Long userId);
    ClassGroupResponse leaveGroup(Long groupId, Long userId);
    void deleteGroup(Long groupId, Long userId);
    List<GroupMemberResponse> getGroupMembers(Long groupId, Long userId);

}
