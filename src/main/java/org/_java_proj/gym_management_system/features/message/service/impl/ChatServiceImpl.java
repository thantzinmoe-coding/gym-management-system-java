package org._java_proj.gym_management_system.features.message.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org._java_proj.gym_management_system.config.response.dto.PaginatedApiResponse;
import org._java_proj.gym_management_system.config.response.dto.PaginationMeta;
import org._java_proj.gym_management_system.features.message.dto.request.ChatMessageRequest;
import org._java_proj.gym_management_system.features.message.dto.response.ChatMessageResponse;
import org._java_proj.gym_management_system.features.message.dto.response.ChatRoomResponse;
import org._java_proj.gym_management_system.features.message.dto.response.WebSocketMessage;
import org._java_proj.gym_management_system.features.message.repository.ChatMessageRepository;
import org._java_proj.gym_management_system.features.message.repository.ChatRoomRepository;
import org._java_proj.gym_management_system.features.message.service.ChatService;
import org._java_proj.gym_management_system.features.message.service.OnlineUserService;
import org._java_proj.gym_management_system.features.users.repository.UserRepository;
import org._java_proj.gym_management_system.model.ChatMessage;
import org._java_proj.gym_management_system.model.ChatRoom;
import org._java_proj.gym_management_system.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final SimpMessageSendingOperations messagingTemplate;
    private final OnlineUserService onlineUserService;

    @Transactional
    public ChatMessageResponse sendPrivateMessage(Long senderId, ChatMessageRequest request) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found: " + senderId));
        User recipient = userRepository.findById(request.getRecipientId())
                .orElseThrow(() -> new IllegalArgumentException("Recipient not found: " + request.getRecipientId()));

        ChatMessage message = ChatMessage.builder()
                .senderId(senderId)
                .recipientId(request.getRecipientId())
                .content(request.getContent())
                .messageType(request.getMessageType())
                .attachmentUrl(request.getAttachmentUrl())
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(message);

        createOrUpdateChatRoom(senderId, request.getRecipientId(), savedMessage.getId());

        ChatMessageResponse response = convertToMessageResponse(savedMessage);

        messagingTemplate.convertAndSendToUser(
                String.valueOf(recipient.getId()),
                "/queue/messages",
                WebSocketMessage.of("NEW_MESSAGE", response)
        );

        // Notify sender
        messagingTemplate.convertAndSendToUser(
                sender.getEmail(),
                "/queue/messages",
                WebSocketMessage.of("NEW_MESSAGE", response)
        );

        log.info("Private message sent from {} to {}", senderId, request.getRecipientId());
        return response;
    }



    @Override
    public PaginatedApiResponse<ChatMessageResponse> getPrivateChatHistory(
            Long userId, Long otherUserId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ChatMessage> messagesPage =
                chatMessageRepository.findPrivateMessagesBetweenUsers(userId, otherUserId, pageable);

        // Convert ChatMessage → ChatMessageResponse
        List<ChatMessageResponse> messageDtos = messagesPage.getContent()
                .stream()
                .map(this::convertToMessageResponse)
                .collect(Collectors.toList());

        // Build pagination metadata
        PaginationMeta meta = new PaginationMeta();
        meta.setTotalItems(messagesPage.getTotalElements());
        meta.setTotalPages(messagesPage.getTotalPages());
        meta.setCurrentPage(pageable.getPageNumber() + 1);

        return PaginatedApiResponse.<ChatMessageResponse>builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .message("Private chat history fetched successfully.")
                .meta(meta)
                .data(messageDtos)
                .build();
    }


    public List<ChatRoomResponse> getChatRooms(Long userId) {
        List<ChatRoom> chatRooms = chatRoomRepository.findByUserIdOrderByLastMessageAtDesc(userId);
        return chatRooms.stream()
                .map(room -> convertToChatRoomResponse(room, userId))
                .collect(Collectors.toList());
    }

    @Transactional
    public void markMessagesAsRead(Long userId, Long senderId) {
        chatMessageRepository.markPrivateMessagesAsRead(userId, senderId);
        log.info("Messages marked as read for recipient {} from sender {}", userId, senderId);
    }

    private void createOrUpdateChatRoom(Long user1Id, Long user2Id, Long messageId) {
        Optional<ChatRoom> existingRoom = chatRoomRepository.findByUsers(user1Id, user2Id);

        if (existingRoom.isPresent()) {
            ChatRoom room = existingRoom.get();
            room.setLastMessageId(messageId);
            room.setLastMessageAt(LocalDateTime.now());
            chatRoomRepository.save(room);
        } else {
            ChatRoom newRoom = ChatRoom.builder()
                    .roomIdentifier("user" + Math.min(user1Id, user2Id) + "_user" + Math.max(user1Id, user2Id))
                    .user1Id(user1Id)
                    .user2Id(user2Id)
                    .lastMessageId(messageId)
                    .lastMessageAt(LocalDateTime.now())
                    .build();
            chatRoomRepository.save(newRoom);
        }
    }

    private ChatMessageResponse convertToMessageResponse(ChatMessage message) {
        User sender = userRepository.findById(message.getSenderId()).orElse(null);

        return ChatMessageResponse.builder()
                .id(message.getId())
                .senderId(message.getSenderId())
                .senderName(sender != null ? sender.getProfile().getName() : "Unknown")
                .senderAvatarUrl(sender != null ? sender.getProfile().getProfilePic() : null)
                .recipientId(message.getRecipientId())
                .content(message.getContent())
                .messageType(message.getMessageType())
                .attachmentUrl(message.getAttachmentUrl())
                .isRead(message.isRead())
                .isEdited(message.isEdited())
                .editedAt(message.getUpdatedAt())
                .createdAt(message.getCreatedAt())
                .build();
    }

    private ChatRoomResponse convertToChatRoomResponse(ChatRoom room, Long currentUserId) {
        Long otherUserId = room.getUser1Id().equals(currentUserId) ? room.getUser2Id() : room.getUser1Id();
        User otherUser = userRepository.findById(otherUserId).orElse(null);

        ChatMessageResponse lastMessage = null;
        if (room.getLastMessageId() != null) {
            Optional<ChatMessage> message = chatMessageRepository.findById(room.getLastMessageId());
            if (message.isPresent()) {
                lastMessage = convertToMessageResponse(message.get());
            }
        }

        Long unreadCount = chatMessageRepository.countUnreadPrivateMessages(currentUserId);
        Boolean isOnline = onlineUserService.isUserOnline(otherUserId);

        return ChatRoomResponse.builder()
                .id(room.getId())
                .otherUserId(otherUserId)
                .otherUserName(otherUser != null ? otherUser.getProfile().getName() : "Unknown")
                .otherUserAvatarUrl(otherUser != null ? otherUser.getProfile().getProfilePic() : null)
                .otherUserOnline(isOnline)
                .lastMessage(lastMessage)
                .unreadCount(Math.toIntExact(unreadCount))
                .lastMessageAt(room.getLastMessageAt())
                .createdAt(room.getCreatedAt())
                .build();
    }
}