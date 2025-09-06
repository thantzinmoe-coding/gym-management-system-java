package org._java_proj.gym_management_system.features.message.controller;

import lombok.RequiredArgsConstructor;
import org._java_proj.gym_management_system.features.message.service.OnlineUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.base.path}/online-status")
@RequiredArgsConstructor
@Tag(name = "Online Status", description = "Check online status of users")
public class OnlineStatusController {

    private final OnlineUserService onlineUserService;

    @Operation(
            summary = "Check multiple users' online status",
            description = "Returns a map of userId -> online status (true/false)",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(type = "array", implementation = Long.class),
                            examples = @ExampleObject(
                                    name = "User IDs Example",
                                    value = "[101, 102, 103]"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Map of user IDs to online status",
                            content = @Content(
                                    schema = @Schema(type = "object", implementation = Map.class),
                                    examples = @ExampleObject(
                                            name = "Status Map Example",
                                            value = "{ \"101\": true, \"102\": false, \"103\": true }"
                                    )
                            )
                    )
            }
    )
    @PostMapping("/check")
    public ResponseEntity<Map<Long, Boolean>> checkUsersOnlineStatus(
            @RequestBody List<Long> userIds) {
        List<Long> onlineUsers = onlineUserService.getOnlineUsers(userIds);

        Map<Long, Boolean> statusMap = userIds.stream()
                .collect(Collectors.toMap(
                        userId -> userId,
                        onlineUsers::contains
                ));

        return ResponseEntity.ok(statusMap);
    }

    @Operation(
            summary = "Check a single user's online status",
            description = "Returns a map with key 'online' indicating whether the user is online",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Online status of the user",
                            content = @Content(
                                    schema = @Schema(type = "object"),
                                    examples = @ExampleObject(
                                            name = "Single User Status Example",
                                            value = "{ \"online\": true }"
                                    )
                            )
                    )
            }
    )
    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Boolean>> isUserOnline(@PathVariable Long userId) {
        boolean isOnline = onlineUserService.isUserOnline(userId);
        return ResponseEntity.ok(Map.of("online", isOnline));
    }


}

