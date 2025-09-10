package org._java_proj.gym_management_system.features.message.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org._java_proj.gym_management_system.features.message.repository.GroupRepository;
import org._java_proj.gym_management_system.model.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${api.base.path}/groups")
@RequiredArgsConstructor
@Tag(name = "Groups", description = "Endpoints for managing groups")
public class GroupController {

    @Autowired
    private GroupRepository groupRepository;

    @Operation(
            summary = "Create a new group",
            description = "Creates a new group with a generated UUID",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = Group.class),
                            examples = @ExampleObject(
                                    name = "Create Group Example",
                                    value = "{ \"name\": \"Study Buddies\" }"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Group created successfully",
                            content = @Content(schema = @Schema(implementation = Group.class),
                                    examples = @ExampleObject(
                                            name = "Group Response Example",
                                            value = "{ \"groupId\": \"123e4567-e89b-12d3-a456-426614174000\", \"name\": \"Study Buddies\" }"
                                    )
                            ))
            }
    )
    @PostMapping
    public Group createGroup(@RequestBody Group group) {
        group.setGroupId(UUID.randomUUID().toString());
        return groupRepository.save(group);
    }

    @Operation(
            summary = "Get all groups",
            description = "Fetches a list of all groups",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of groups",
                            content = @Content(schema = @Schema(implementation = Group.class),
                                    examples = @ExampleObject(
                                            name = "Groups List Example",
                                            value = "[ { \"groupId\": \"123e4567-e89b-12d3-a456-426614174000\", \"name\": \"Study Buddies\" }, { \"groupId\": \"223e4567-e89b-12d3-a456-426614174111\", \"name\": \"Chess Club\" } ]"
                                    )
                            ))
            }
    )
    @GetMapping
    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }
}

