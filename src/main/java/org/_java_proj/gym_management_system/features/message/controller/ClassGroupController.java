package org._java_proj.gym_management_system.features.message.controller;

import lombok.RequiredArgsConstructor;
import org._java_proj.gym_management_system.features.message.dto.request.ClassGroupCreateRequest;
import org._java_proj.gym_management_system.features.message.dto.response.ClassGroupResponse;
import org._java_proj.gym_management_system.features.message.dto.response.GroupMemberResponse;
import org._java_proj.gym_management_system.features.message.service.ClassGroupService;
import org._java_proj.gym_management_system.model.UserDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("${api.base.path}/class-group")
@RequiredArgsConstructor
@Tag(name = "Class Groups", description = "Endpoints for managing class groups")
public class ClassGroupController {
    private final ClassGroupService classGroupService;

    @Operation(
            summary = "Create a new class group",
            requestBody = @RequestBody(
                    description = "Group creation request",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ClassGroupCreateRequest.class),
                            examples = @ExampleObject(
                                    name = "Create Group Example",
                                    value = "{ \"name\": \"Math Study Group\", \"description\": \"Group for math lovers\", \"isPublic\": true }"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Group created successfully",
                            content = @Content(schema = @Schema(implementation = ClassGroupResponse.class)))
            }
    )
    @PostMapping
    public ResponseEntity<ClassGroupResponse> createGroup(
            @RequestBody ClassGroupCreateRequest request,
            @AuthenticationPrincipal UserDetail userDetails) {
        ClassGroupResponse group = classGroupService.createClassGroup(request, userDetails.getUser().getId());
        return ResponseEntity.ok(group);
    }

    @Operation(summary = "Get all public groups")
    @GetMapping
    public ResponseEntity<List<ClassGroupResponse>> getAllGroups(@AuthenticationPrincipal UserDetail userDetails) {
        List<ClassGroupResponse> groups = classGroupService.getAllPublicGroups(userDetails.getUser().getId());
        return ResponseEntity.ok(groups);
    }

    @Operation(summary = "Get all groups the authenticated user belongs to")
    @GetMapping("/my-groups")
    public ResponseEntity<List<ClassGroupResponse>> getUserGroups(@AuthenticationPrincipal UserDetail userDetails) {
        List<ClassGroupResponse> groups = classGroupService.getUserGroups(userDetails.getUser().getId());
        return ResponseEntity.ok(groups);
    }

    @Operation(
            summary = "Search groups",
            parameters = {
                    @io.swagger.v3.oas.annotations.Parameter(
                            name = "q", description = "Search query", example = "math"
                    )
            }
    )
    @GetMapping("/search")
    public ResponseEntity<List<ClassGroupResponse>> searchGroups(
            @RequestParam String q,
            @AuthenticationPrincipal UserDetail userDetails) {
        List<ClassGroupResponse> groups = classGroupService.searchGroups(q, userDetails.getUser().getId());
        return ResponseEntity.ok(groups);
    }

    @Operation(summary = "Get group by ID")
    @GetMapping("/{groupId}")
    public ResponseEntity<ClassGroupResponse> getGroupById(
            @PathVariable Long groupId,
            @AuthenticationPrincipal UserDetail userDetails) {
        ClassGroupResponse group = classGroupService.getGroupById(groupId, userDetails.getUser().getId());
        return ResponseEntity.ok(group);
    }

    @Operation(summary = "Join a group")
    @PostMapping("/{groupId}/join")
    public ResponseEntity<ClassGroupResponse> joinGroup(
            @PathVariable Long groupId,
            @AuthenticationPrincipal UserDetail userDetails) {
        ClassGroupResponse group = classGroupService.joinGroup(groupId, userDetails.getUser().getId());
        return ResponseEntity.ok(group);
    }

    @Operation(summary = "Leave a group")
    @PostMapping("/{groupId}/leave")
    public ResponseEntity<ClassGroupResponse> leaveGroup(
            @PathVariable Long groupId,
            @AuthenticationPrincipal UserDetail userDetails) {
        ClassGroupResponse group = classGroupService.leaveGroup(groupId, userDetails.getUser().getId());
        return ResponseEntity.ok(group);
    }

    @Operation(summary = "Delete a group")
    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> deleteGroup(
            @PathVariable Long groupId,
            @AuthenticationPrincipal UserDetail userDetails) {
        classGroupService.deleteGroup(groupId, userDetails.getUser().getId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get members of a group")
    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<GroupMemberResponse>> getGroupMembers(
            @PathVariable Long groupId,
            @AuthenticationPrincipal UserDetail userDetails) {
        List<GroupMemberResponse> members = classGroupService.getGroupMembers(groupId, userDetails.getUser().getId());
        return ResponseEntity.ok(members);
    }
}

