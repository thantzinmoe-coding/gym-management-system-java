package org._java_proj.gym_management_system.features.message.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org._java_proj.gym_management_system.config.response.dto.PaginatedApiResponse;
import org._java_proj.gym_management_system.config.response.util.ResponseUtils;
import org._java_proj.gym_management_system.features.message.dto.request.ChatMessageRequest;
import org._java_proj.gym_management_system.features.message.dto.response.ChatRoomResponse;
import org._java_proj.gym_management_system.features.message.dto.response.ChatMessageResponse;
import org._java_proj.gym_management_system.features.message.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.base.path}/chat")
@RequiredArgsConstructor
@Tag(name = "Chat Management", description = "APIs for managing user chats and chat rooms.")
public class ChatRestController {

    private final ChatService chatService;

    @Operation(summary = "Get user chat rooms",
            description = "Retrieves all private chat rooms associated with the authenticated user, including last message and unread count.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved chat rooms.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ChatRoomResponse.class),
                                    examples = @ExampleObject(name = "Chat Room List Example", value = """
                               [
                                   {
                                       "id": 1,
                                       "otherUserId": 102,
                                       "otherUserName": "John Doe",
                                       "otherUserAvatarUrl": "https://placehold.co/40x40/33FF57/white?text=JD",
                                       "otherUserOnline": true,
                                       "lastMessage": {
                                           "id": 1001,
                                           "senderId": 102,
                                           "senderName": "John Doe",
                                           "senderAvatarUrl": "https://placehold.co/40x40/33FF57/white?text=JD",
                                           "recipientId": 101,
                                           "content": "Hey, how are you?",
                                           "messageType": "TEXT",
                                           "attachmentUrl": null,
                                           "isRead": true,
                                           "isEdited": false,
                                           "editedAt": null,
                                           "createdAt": "2024-08-26T10:00:00"
                                       },
                                       "unreadCount": 2,
                                       "lastMessageAt": "2024-08-26T10:00:00",
                                       "createdAt": "2024-08-25T09:00:00"
                                   }
                               ]
                               """)
                            ))
            })
    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomResponse>> getChatRooms(@AuthenticationPrincipal String userId) {
        Long id = Long.parseLong(userId);
        List<ChatRoomResponse> chatRooms = chatService.getChatRooms(id);
        return ResponseEntity.ok(chatRooms);
    }

    @GetMapping("/private/{otherUserId}")
    @Operation(summary = "Get private chat messages (paginated)")
    public ResponseEntity<PaginatedApiResponse<ChatMessageResponse>> getPrivateChatHistory(
            @Parameter(description = "ID of the other user for private chat.") @PathVariable Long otherUserId,
            @Parameter(description = "Page number for pagination (default: 0).") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of messages per page (default: 20).") @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal String userId,
            HttpServletRequest servletRequest) {

        Long senderId = Long.parseLong(userId);

        PaginatedApiResponse<ChatMessageResponse> response =
                chatService.getPrivateChatHistory(senderId, otherUserId, page, size);

        return ResponseUtils.buildPaginatedResponse(servletRequest, response);
    }


    @Operation(summary = "Mark messages as read",
            description = "Marks all unread messages from a specific sender in a private chat as read for the authenticated user.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Request body to mark private messages as read.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "Mark Private Messages Read", value = "{\"senderId\": 102}"))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Messages successfully marked as read.",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "Success Response", value = "{\"message\": \"Messages marked as read\"}")))
            })
    @PostMapping("/mark-read")
    public ResponseEntity<Map<String, String>> markMessagesAsRead(
            @RequestBody Map<String, Long> request,
            @AuthenticationPrincipal String userId) {
        Long id = Long.parseLong(userId);
        Long senderId = request.get("senderId");
        if (senderId == null) {
            throw new IllegalArgumentException("senderId is required");
        }
        chatService.markMessagesAsRead(id, senderId);
        return ResponseEntity.ok(Map.of("message", "Messages marked as read"));
    }

    @Operation(summary = "Send a private chat message",
            description = "Sends a new private message to a recipient.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Details of the message to be sent.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ChatMessageRequest.class),
                            examples = @ExampleObject(name = "Private Text Message", value = """
                               {
                                   "content": "Hello there! How are you?",
                                   "recipientId": 102,
                                   "messageType": "TEXT",
                                   "attachmentUrl": null
                               }
                               """)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Message successfully sent.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ChatMessageResponse.class),
                                    examples = @ExampleObject(name = "Sent Message Response", value = """
                               {
                                   "id": 1003,
                                   "senderId": 101,
                                   "senderName": "Current User",
                                   "senderAvatarUrl": "https://placehold.co/40x40/FF5733/white?text=CU",
                                   "recipientId": 102,
                                   "content": "Hello there! How are you?",
                                   "messageType": "TEXT",
                                   "attachmentUrl": null,
                                   "isRead": false,
                                   "isEdited": false,
                                   "editedAt": null,
                                   "createdAt": "2024-08-26T10:30:00"
                               }
                               """)
                            ))
            })
    @PostMapping("/send")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @Valid @RequestBody ChatMessageRequest request,
            @AuthenticationPrincipal String userId) {
        Long id = Long.parseLong(userId);
        ChatMessageResponse response = chatService.sendPrivateMessage(id, request);
        return ResponseEntity.ok(response);
    }
}