package org._java_proj.gym_management_system.features.message.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org._java_proj.gym_management_system.features.message.dto.request.ChatMessageRequest;
import org._java_proj.gym_management_system.features.message.dto.response.ChatMessageResponse;
import org._java_proj.gym_management_system.features.message.dto.response.ChatRoomResponse;
import org._java_proj.gym_management_system.features.message.dto.response.WebSocketMessage;
import org._java_proj.gym_management_system.features.message.repository.ChatMessageRepository;
import org._java_proj.gym_management_system.features.message.repository.ChatRoomRepository;
import org._java_proj.gym_management_system.features.message.repository.ClassGroupRepository;
import org._java_proj.gym_management_system.features.message.repository.GroupMemberRepository;
import org._java_proj.gym_management_system.features.message.service.ChatService;
import org._java_proj.gym_management_system.features.message.service.OnlineUserService;
import org._java_proj.gym_management_system.features.users.repository.UserRepository;
import org._java_proj.gym_management_system.model.ChatMessage;
import org._java_proj.gym_management_system.model.ChatRoom;
import org._java_proj.gym_management_system.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final ClassGroupRepository classGroupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final SimpMessageSendingOperations messagingTemplate;
    private final OnlineUserService onlineUserService;

    @Transactional
    public ChatMessageResponse sendPrivateMessage(Long senderId, ChatMessageRequest request) {
        // Validate recipient exists and allows messages
        User recipient = userRepository.findById(request.getRecipientId())
                .orElseThrow(() -> new RuntimeException("Recipient not found"));

        // Create and save message
        ChatMessage message = ChatMessage.builder()
                .senderId(senderId)
                .recipientId(request.getRecipientId())
                .content(request.getContent())
                .messageType(request.getMessageType())
                .attachmentUrl(request.getAttachmentUrl())
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(message);

        // Create or update chat room
        createOrUpdateChatRoom(senderId, request.getRecipientId(), savedMessage.getId());

        // Convert to response
        ChatMessageResponse response = convertToMessageResponse(savedMessage);

        // Send real-time message to recipient
        messagingTemplate.convertAndSendToUser(
                recipient.getEmail(),
                "/queue/messages",
                WebSocketMessage.of("NEW_MESSAGE", response)
        );

        log.info("Private message sent from {} to {}", senderId, request.getRecipientId());
        return response;
    }

    @Transactional
    public ChatMessageResponse sendGroupMessage(Long senderId, ChatMessageRequest request) {
        // Validate study group exists and user is member
        classGroupRepository.findById(request.getStudyGroupId())
                .orElseThrow(() -> new RuntimeException("Study group not found"));

        boolean isMember = groupMemberRepository.existsByUserIdAndGroupId(senderId, request.getStudyGroupId());
        if (!isMember) {
            throw new RuntimeException("User is not a member of this study group");
        }

        // Create and save message
        ChatMessage message = ChatMessage.builder()
                .senderId(senderId)
                .studyGroupId(request.getStudyGroupId())
                .content(request.getContent())
                .messageType(request.getMessageType())
                .attachmentUrl(request.getAttachmentUrl())
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(message);
        ChatMessageResponse response = convertToMessageResponse(savedMessage);

        // Send real-time message to all group members
        messagingTemplate.convertAndSend(
                "/topic/group/" + request.getStudyGroupId(),
                WebSocketMessage.of("NEW_GROUP_MESSAGE", response)
        );

        // Create notifications for all group members except sender
        List<Long> memberIds = groupMemberRepository.findUserIdsByGroupId(request.getStudyGroupId())
                .stream()
                .filter(id -> !id.equals(senderId))
                .toList();

        log.info("Group message sent by {} to group {}", senderId, request.getStudyGroupId());
        return response;
    }

    public Page<ChatMessageResponse> getPrivateChatHistory(Long userId, Long otherUserId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ChatMessage> messages = chatMessageRepository.findPrivateMessagesBetweenUsers(userId, otherUserId, pageable);
        return messages.map(this::convertToMessageResponse);
    }

    public Page<ChatMessageResponse> getGroupChatHistory(Long studyGroupId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ChatMessage> messages = chatMessageRepository.findGroupMessages(studyGroupId, pageable);
        return messages.map(this::convertToMessageResponse);
    }

    public List<ChatRoomResponse> getChatRooms(Long userId) {
        List<ChatRoom> chatRooms = chatRoomRepository.findByUserIdOrderByLastMessageAtDesc(userId);
        return chatRooms.stream()
                .map(room -> convertToChatRoomResponse(room, userId))
                .collect(Collectors.toList());
    }

    @Transactional
    public void markMessagesAsRead(Long userId, Long senderId, Long studyGroupId) {
        if (studyGroupId != null) {
            chatMessageRepository.markGroupMessagesAsRead(studyGroupId, userId);
        } else if (senderId != null) {
            chatMessageRepository.markPrivateMessagesAsRead(userId, senderId);
        }
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
                .studyGroupId(message.getStudyGroupId())
                .content(message.getContent())
                .messageType(message.getMessageType())
                .attachmentUrl(message.getAttachmentUrl())
                .isRead(message.getIsRead())
                .isEdited(message.getIsEdited())
                .editedAt(message.getUpdatedAt())
                .createdAt(message.getCreatedAt())
                .build();
    }

    private ChatRoomResponse convertToChatRoomResponse(ChatRoom room, Long currentUserId) {
        Long otherUserId = room.getUser1Id().equals(currentUserId) ? room.getUser2Id() : room.getUser1Id();
        User otherUser = userRepository.findById(otherUserId).orElse(null);

        ChatMessageResponse lastMessage = null;
        if (room.getLastMessage() != null) {
            lastMessage = convertToMessageResponse(room.getLastMessage());
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
