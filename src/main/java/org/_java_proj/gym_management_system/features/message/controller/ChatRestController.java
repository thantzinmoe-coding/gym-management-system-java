package org._java_proj.gym_management_system.features.message.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org._java_proj.gym_management_system.features.message.dto.request.ChatMessageRequest;
import org._java_proj.gym_management_system.features.message.dto.response.ChatMessageResponse;
import org._java_proj.gym_management_system.features.message.dto.response.ChatRoomResponse;
import org._java_proj.gym_management_system.features.message.service.ChatService;
import org._java_proj.gym_management_system.model.User;
import org._java_proj.gym_management_system.model.UserDetail;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for managing chat functionalities, including private and group messages,
 * chat room retrieval, and message read status.
 */
@RestController
@RequestMapping("${api.base.path}/chat")
@RequiredArgsConstructor
@Tag(name = "Chat Management", description = "APIs for managing user chats and chat rooms.")
public class ChatRestController {

    private final ChatService chatService;

    /**
     * Retrieves a list of chat rooms for the authenticated user.
     *
     * @param authentication The current user's authentication principal.
     * @return A list of ChatRoomResponse objects.
     */
    @Operation(summary = "Get user chat rooms",
            description = "Retrieves all chat rooms (private and group) associated with the authenticated user, including last message and unread count.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved chat rooms.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ChatRoomResponse.class),
                                    examples = @ExampleObject(name = "Chat Room List Example", value = """
                               [
                                   {
                                       "id": 1,
                                       "roomName": "Private Chat with John Doe",
                                       "lastMessageContent": "Hey, how are you?",
                                       "lastMessageTimestamp": "2024-08-26T10:00:00",
                                       "unreadMessageCount": 2,
                                       "participants": [
                                           {"id": 101, "name": "Current User", "avatarUrl": "https://placehold.co/40x40/FF5733/white?text=CU"},
                                           {"id": 102, "name": "John Doe", "avatarUrl": "https://placehold.co/40x40/33FF57/white?text=JD"}
                                       ],
                                       "isGroupChat": false,
                                       "targetId": 102
                                   },
                                   {
                                       "id": 2,
                                       "roomName": "Study Group Alpha",
                                       "lastMessageContent": "Don't forget the meeting!",
                                       "lastMessageTimestamp": "2024-08-26T09:30:00",
                                       "unreadMessageCount": 0,
                                       "participants": [
                                           {"id": 101, "name": "Current User", "avatarUrl": "https://placehold.co/40x40/FF5733/white?text=CU"},
                                           {"id": 103, "name": "Jane Smith", "avatarUrl": "https://placehold.co/40x40/3357FF/white?text=JS"},
                                           {"id": 104, "name": "Mike Johnson", "avatarUrl": "https://placehold.co/40x40/FF33CC/white?text=MJ"}
                                       ],
                                       "isGroupChat": true,
                                       "targetId": 201
                                   }
                               ]
                               """)
                            ))
            })
    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomResponse>> getChatRooms(Authentication authentication) {
        UserDetail user = (UserDetail) authentication.getPrincipal();
        List<ChatRoomResponse> chatRooms = chatService.getChatRooms(user.getUser().getId());
        return ResponseEntity.ok(chatRooms);
    }

    /**
     * Retrieves the private chat history between the authenticated user and another user.
     *
     * @param otherUserId The ID of the other user in the private chat.
     * @param page        The page number for pagination (default: 0).
     * @param size        The number of messages per page (default: 20).
     * @param authentication The current user's authentication principal.
     * @return A paginated list of ChatMessageResponse objects.
     */
    @Operation(summary = "Get private chat history",
            description = "Retrieves a paginated history of private messages with a specific user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved private chat messages.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Page.class), // Indicate it's a Page
                                    examples = @ExampleObject(name = "Private Chat History Example", value = """
                               {
                                   "content": [
                                       {
                                           "id": 1001,
                                           "senderId": 102,
                                           "senderName": "John Doe",
                                           "senderAvatarUrl": "https://placehold.co/40x40/33FF57/white?text=JD",
                                           "recipientId": 101,
                                           "studyGroupId": null,
                                           "content": "Hey, how are you?",
                                           "messageType": "TEXT",
                                           "attachmentUrl": null,
                                           "isRead": true,
                                           "isEdited": false,
                                           "editedAt": null,
                                           "createdAt": "2024-08-26T10:00:00"
                                       },
                                       {
                                           "id": 1002,
                                           "senderId": 101,
                                           "senderName": "Current User",
                                           "senderAvatarUrl": "https://placehold.co/40x40/FF5733/white?text=CU",
                                           "recipientId": 102,
                                           "studyGroupId": null,
                                           "content": "I'm good, thanks! What about you?",
                                           "messageType": "TEXT",
                                           "attachmentUrl": null,
                                           "isRead": true,
                                           "isEdited": false,
                                           "editedAt": null,
                                           "createdAt": "2024-08-26T10:01:00"
                                       }
                                   ],
                                   "pageable": {
                                       "pageNumber": 0,
                                       "pageSize": 20,
                                       "sort": { "empty": true, "sorted": false, "unsorted": true },
                                       "offset": 0,
                                       "paged": true,
                                       "unpaged": false
                                   },
                                   "last": true,
                                   "totalPages": 1,
                                   "totalElements": 2,
                                   "size": 20,
                                   "number": 0,
                                   "sort": { "empty": true, "sorted": false, "unsorted": true },
                                   "first": true,
                                   "numberOfElements": 2,
                                   "empty": false
                               }
                               """)
                            ))
            })
    @GetMapping("/private/{otherUserId}")
    public ResponseEntity<Page<ChatMessageResponse>> getPrivateChatHistory(
            @Parameter(description = "ID of the other user for private chat.") @PathVariable Long otherUserId,
            @Parameter(description = "Page number for pagination (default: 0).") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of messages per page (default: 20).") @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        UserDetail user = (UserDetail) authentication.getPrincipal();
        Page<ChatMessageResponse> messages = chatService.getPrivateChatHistory(user.getUser().getId(), otherUserId, page, size);
        return ResponseEntity.ok(messages);
    }

    /**
     * Retrieves the group chat history for a specific study group.
     *
     * @param studyGroupId The ID of the study group.
     * @param page         The page number for pagination (default: 0).
     * @param size         The number of messages per page (default: 20).
     * @return A paginated list of ChatMessageResponse objects.
     */
    @Operation(summary = "Get group chat history",
            description = "Retrieves a paginated history of messages for a specific study group.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved group chat messages.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Page.class),
                                    examples = @ExampleObject(name = "Group Chat History Example", value = """
                               {
                                   "content": [
                                       {
                                           "id": 2001,
                                           "senderId": 101,
                                           "senderName": "Current User",
                                           "senderAvatarUrl": "https://placehold.co/40x40/FF5733/white?text=CU",
                                           "recipientId": null,
                                           "studyGroupId": 201,
                                           "content": "Hey team, when is our next study session?",
                                           "messageType": "TEXT",
                                           "attachmentUrl": null,
                                           "isRead": true,
                                           "isEdited": false,
                                           "editedAt": null,
                                           "createdAt": "2024-08-26T09:00:00"
                                       },
                                       {
                                           "id": 2002,
                                           "senderId": 103,
                                           "senderName": "Jane Smith",
                                           "senderAvatarUrl": "https://placehold.co/40x40/3357FF/white?text=JS",
                                           "recipientId": null,
                                           "studyGroupId": 201,
                                           "content": "Tomorrow at 2 PM, don't forget!",
                                           "messageType": "TEXT",
                                           "attachmentUrl": null,
                                           "isRead": true,
                                           "isEdited": false,
                                           "editedAt": null,
                                           "createdAt": "2024-08-26T09:05:00"
                                       }
                                   ],
                                   "pageable": {
                                       "pageNumber": 0,
                                       "pageSize": 20,
                                       "sort": { "empty": true, "sorted": false, "unsorted": true },
                                       "offset": 0,
                                       "paged": true,
                                       "unpaged": false
                                   },
                                   "last": false,
                                   "totalPages": 5,
                                   "totalElements": 98,
                                   "size": 20,
                                   "number": 0,
                                   "sort": { "empty": true, "sorted": false, "unsorted": true },
                                   "first": true,
                                   "numberOfElements": 20,
                                   "empty": false
                               }
                               """)
                            ))
            })
    @GetMapping("/group/{studyGroupId}")
    public ResponseEntity<Page<ChatMessageResponse>> getGroupChatHistory(
            @Parameter(description = "ID of the study group for group chat.") @PathVariable Long studyGroupId,
            @Parameter(description = "Page number for pagination (default: 0).") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of messages per page (default: 20).") @RequestParam(defaultValue = "20") int size) {
        Page<ChatMessageResponse> messages = chatService.getGroupChatHistory(studyGroupId, page, size);
        return ResponseEntity.ok(messages);
    }

    /**
     * Marks messages as read for a specific sender in a private chat or for a specific study group.
     *
     * @param request A map containing either "senderId" for private chats or "studyGroupId" for group chats.
     * @param authentication The current user's authentication principal.
     * @return A map indicating the success of the operation.
     */
    @Operation(summary = "Mark messages as read",
            description = "Marks all unread messages from a specific sender (private chat) or within a specific study group (group chat) as read for the authenticated user.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Request body to mark messages as read. Provide either 'senderId' for private chat or 'studyGroupId' for group chat.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Mark Private Messages Read", summary = "For private chat", value = "{\"senderId\": 102, \"studyGroupId\": null}"),
                                    @ExampleObject(name = "Mark Group Messages Read", summary = "For group chat", value = "{\"senderId\": null, \"studyGroupId\": 201}")
                            })
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Messages successfully marked as read.",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "Success Response", value = "{\"message\": \"Messages marked as read\"}")))
            })
    @PostMapping("/mark-read")
    public ResponseEntity<Map<String, String>> markMessagesAsRead(
            @RequestBody Map<String, Long> request,
            Authentication authentication) {
        UserDetail user = (UserDetail) authentication.getPrincipal();
        Long senderId = request.get("senderId");
        Long studyGroupId = request.get("studyGroupId");

        chatService.markMessagesAsRead(user.getUser().getId(), senderId, studyGroupId);
        return ResponseEntity.ok(Map.of("message", "Messages marked as read"));
    }

    /**
     * Sends a new private or group chat message.
     *
     * @param request The ChatMessageRequest containing message details.
     * @param authentication The current user's authentication principal.
     * @return The ChatMessageResponse of the sent message.
     */
    @Operation(summary = "Send a chat message",
            description = "Sends a new private message to a recipient or a group message to a study group.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Details of the message to be sent.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ChatMessageRequest.class),
                            examples = {
                                    @ExampleObject(name = "Private Text Message", summary = "Send a text message to a user", value = """
                               {
                                   "content": "Hello there! How are you?",
                                   "recipientId": 102,
                                   "studyGroupId": null,
                                   "messageType": "TEXT",
                                   "attachmentUrl": null
                               }
                               """),
                                    @ExampleObject(name = "Group Image Message", summary = "Send an image message to a group", value = """
                               {
                                   "content": "Check out this image!",
                                   "recipientId": null,
                                   "studyGroupId": 201,
                                   "messageType": "IMAGE",
                                   "attachmentUrl": "https://example.com/image_attachment.jpg"
                               }
                               """)
                            })
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
                                   "studyGroupId": null,
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
            Authentication authentication) {
        UserDetail user = (UserDetail) authentication.getPrincipal();

        ChatMessageResponse response;
        if (request.getStudyGroupId() != null) {
            response = chatService.sendGroupMessage(user.getUser().getId(), request);
        } else {
            response = chatService.sendPrivateMessage(user.getUser().getId(), request);
        }

        return ResponseEntity.ok(response);
    }
}
