package org.java_proj.gym_management_system.features.message.service.impl;

import lombok.RequiredArgsConstructor;
import org.java_proj.gym_management_system.features.message.dto.request.ChatMessageRequest;
import org.java_proj.gym_management_system.features.message.dto.response.ChatMessageResponse;
import org.java_proj.gym_management_system.features.message.dto.response.ChatRoomResponse;
import org.java_proj.gym_management_system.features.message.repository.ChatMessageRepository;
import org.java_proj.gym_management_system.features.message.repository.ChatRoomRepository;
import org.java_proj.gym_management_system.features.message.service.ChatService;
import org.java_proj.gym_management_system.features.users.repository.UserRepository;
import org.java_proj.gym_management_system.model.ChatMessage;
import org.java_proj.gym_management_system.model.ChatRoom;
import org.java_proj.gym_management_system.model.Profile;
import org.java_proj.gym_management_system.model.User;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository messageRepo;
    private final ChatRoomRepository roomRepo;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepo; // Inject to find users

    @Override
    @Transactional
    public ChatMessageResponse saveAndSendMessage(String senderEmail, ChatMessageRequest request) {
        // 1. Find REAL Sender using the email from the JWT
        User sender = userRepo.findByEmail(senderEmail)
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found"));
        Long senderId = sender.getId();

        // 2. Find REAL Recipient (We need their email for the WebSocket push)
        User recipient = userRepo.findById(request.getRecipientId())
                .orElseThrow(() -> new IllegalArgumentException("Recipient not found"));

        // 3. Save Message
        ChatMessage message = ChatMessage.builder()
                .senderId(senderId)
                .recipientId(recipient.getId())
                .content(request.getContent())
                .build();
        message = messageRepo.save(message);

        // 4. Update or Create Chat Room
        updateChatRoom(senderId, recipient.getId(), message.getId());

        // 5. Create Response DTO
        ChatMessageResponse response = ChatMessageResponse.builder()
                .id(message.getId())
                .senderId(senderId)
                .recipientId(recipient.getId())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .build();

        // 6. Send via WebSocket to Recipient's EMAIL (Because Principal == Email)
        messagingTemplate.convertAndSendToUser(
                recipient.getEmail(),
                "/queue/messages",
                response
        );

        return response;
    }

    private void updateChatRoom(Long u1, Long u2, Long msgId) {
        ChatRoom room = roomRepo.findByUsers(u1, u2).orElse(
                ChatRoom.builder().user1Id(u1).user2Id(u2).roomIdentifier(u1 + "_" + u2).build()
        );
        room.setLastMessageId(msgId);
        room.setLastMessageAt(LocalDateTime.now());
        roomRepo.save(room);
    }

    @Override
    public List<ChatRoomResponse> getUserRooms(Long userId) {
        // Uses the query from ChatRoomRepository to get rooms for this user
        List<ChatRoom> rooms = roomRepo.findByUserId(userId);

        return rooms.stream().map(room -> {
            // Determine the ID of the other person in the chat
            Long otherId = room.getUser1Id().equals(userId) ? room.getUser2Id() : room.getUser1Id();

            // Replace the mocked "otherName" line in getUserRooms with this:
            // Replace the mocked "otherName" line in getUserRooms with this:
            String otherName = userRepo.findById(otherId)
                    .map(User::getProfile)       // 1. Safely extract the Profile object
                    .map(Profile::getName)       // 2. Safely extract the Name string
                    .orElse("Unknown User");     // 3. Fallback if the User, Profile, or Name is null

            // Fetch the last message object from the database using its ID
            ChatMessageResponse lastMsgResponse = null;
            if (room.getLastMessageId() != null) {
                // Finding the message by ID from ChatMessageRepository
                lastMsgResponse = messageRepo.findById(room.getLastMessageId())
                        .map(this::toResponse) // Map it using your helper method below
                        .orElse(null);
            }

            return ChatRoomResponse.builder()
                    .id(room.getId())
                    .otherUserId(otherId)
                    .otherUserName(otherName)
                    .lastMessage(lastMsgResponse) // This now receives the object it expects![cite: 1]
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public List<ChatMessageResponse> getHistory(Long userId, Long otherUserId) {
        List<ChatMessage> messages = messageRepo.findPrivateMessages(userId, otherUserId);// Implement this query
        return messages.stream().map(this::toResponse).collect(Collectors.toList());
    }

    private ChatMessageResponse toResponse(ChatMessage message) {
        return ChatMessageResponse.builder()
                .id(message.getId())
                .senderId(message.getSenderId())
                .recipientId(message.getRecipientId())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                // Note: If your DTO requires a senderName, you'll need to fetch the User here or handle it in the frontend
                .build();
    }


}