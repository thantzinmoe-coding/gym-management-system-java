package org.java_proj.gym_management_system.features.message.service.impl;

import lombok.RequiredArgsConstructor;
import org.java_proj.gym_management_system.common.constant.Status;
import org.java_proj.gym_management_system.features.bookPackage.repository.BookPackageRepository;
import org.java_proj.gym_management_system.features.message.dto.request.ChatMessageRequest;
import org.java_proj.gym_management_system.features.message.dto.response.ChatMessageResponse;
import org.java_proj.gym_management_system.features.message.dto.response.ChatRoomResponse;
import org.java_proj.gym_management_system.features.message.repository.ChatMessageRepository;
import org.java_proj.gym_management_system.features.message.repository.ChatRoomRepository;
import org.java_proj.gym_management_system.features.message.service.ChatService;
import org.java_proj.gym_management_system.features.message.service.OnlineUserService;
import org.java_proj.gym_management_system.features.users.repository.UserRepository;
import org.java_proj.gym_management_system.model.ChatMessage;
import org.java_proj.gym_management_system.model.ChatRoom;
import org.java_proj.gym_management_system.model.Profile;
import org.java_proj.gym_management_system.model.User;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository messageRepo;
    private final ChatRoomRepository roomRepo;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepo;
    private final BookPackageRepository bookPackageRepository;
    private final OnlineUserService onlineUserService;

    @Override
    @Transactional
    public ChatMessageResponse saveAndSendMessage(String senderEmail, ChatMessageRequest request) {
        if (request == null || request.getRecipientId() == null || request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Recipient and message content are required");
        }

        // 1. Find Sender using email from JWT Principal, or fallback to senderId in request
        User sender = null;
        if (senderEmail != null && !senderEmail.isBlank()) {
            sender = userRepo.findByEmail(senderEmail).orElse(null);
        }
        if (sender == null && request.getSenderId() != null) {
            sender = userRepo.findById(request.getSenderId()).orElse(null);
        }
        if (sender == null) {
            throw new IllegalArgumentException("Authenticated sender user not found");
        }
        Long senderId = sender.getId();

        // Keep sender's online status active
        try {
            onlineUserService.setUserOnline(senderId, null);
        } catch (Exception ignored) {
        }

        // 2. Find Recipient
        User recipient = userRepo.findById(request.getRecipientId())
                .orElseThrow(() -> new IllegalArgumentException("Recipient not found"));

        String senderName = extractUserName(sender);

        // 3. Save Message
        ChatMessage message = ChatMessage.builder()
                .senderId(senderId)
                .recipientId(recipient.getId())
                .content(request.getContent().trim())
                .build();
        message = messageRepo.save(message);

        // 4. Update or Create Chat Room
        updateChatRoom(senderId, recipient.getId(), message.getId());

        // 5. Create Response DTO
        ChatMessageResponse response = ChatMessageResponse.builder()
                .id(message.getId())
                .senderId(senderId)
                .senderName(senderName)
                .recipientId(recipient.getId())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .build();

        // 6. Send via WebSocket to Recipient and Sender (both user queue and direct user topic)
        if (recipient.getEmail() != null) {
            messagingTemplate.convertAndSendToUser(
                    recipient.getEmail(),
                    "/queue/messages",
                    response
            );
        }
        messagingTemplate.convertAndSend("/topic/chat/" + recipient.getId(), response);

        if (sender.getEmail() != null) {
            messagingTemplate.convertAndSendToUser(
                    sender.getEmail(),
                    "/queue/messages",
                    response
            );
        }
        messagingTemplate.convertAndSend("/topic/chat/" + senderId, response);

        return response;
    }

    private void updateChatRoom(Long u1, Long u2, Long msgId) {
        List<ChatRoom> existingRooms = roomRepo.findAllByUsers(u1, u2);
        ChatRoom room = existingRooms.isEmpty()
                ? ChatRoom.builder().user1Id(u1).user2Id(u2).roomIdentifier(u1 + "_" + u2).build()
                : existingRooms.get(0);
        room.setLastMessageId(msgId);
        room.setLastMessageAt(LocalDateTime.now());
        roomRepo.save(room);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatRoomResponse> getUserRooms(Long userId) {
        List<ChatRoom> rooms = roomRepo.findByUserId(userId);
        if (rooms.isEmpty()) {
            return Collections.emptyList();
        }

        // Deduplicate rooms by otherUserId (keep first since ordered by lastMessageAt DESC)
        Map<Long, ChatRoom> roomByOtherUser = new LinkedHashMap<>();
        Set<Long> lastMsgIds = new HashSet<>();
        for (ChatRoom room : rooms) {
            Long otherId = room.getUser1Id().equals(userId) ? room.getUser2Id() : room.getUser1Id();
            if (otherId != null && !otherId.equals(userId) && !roomByOtherUser.containsKey(otherId)) {
                roomByOtherUser.put(otherId, room);
                if (room.getLastMessageId() != null) {
                    lastMsgIds.add(room.getLastMessageId());
                }
            }
        }

        if (roomByOtherUser.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, User> usersById = userRepo.findAllByIdInWithProfile(roomByOtherUser.keySet())
                .stream()
                .collect(Collectors.toMap(User::getId, Function.identity(), (a, b) -> a));

        Map<Long, ChatMessage> messagesById = lastMsgIds.isEmpty()
                ? Collections.emptyMap()
                : messageRepo.findAllById(lastMsgIds)
                        .stream()
                        .collect(Collectors.toMap(ChatMessage::getId, Function.identity(), (a, b) -> a));

        List<ChatRoomResponse> result = new ArrayList<>();
        for (Map.Entry<Long, ChatRoom> entry : roomByOtherUser.entrySet()) {
            Long otherId = entry.getKey();
            ChatRoom room = entry.getValue();
            User otherUser = usersById.get(otherId);

            String otherName = extractUserName(otherUser);
            String otherEmail = otherUser != null ? otherUser.getEmail() : null;
            String otherRole = otherUser != null && otherUser.getRole() != null ? otherUser.getRole().getName() : null;
            String avatarUrl = otherUser != null && otherUser.getProfile() != null ? otherUser.getProfile().getProfilePic() : null;

            ChatMessageResponse lastMsgResponse = null;
            if (room.getLastMessageId() != null && messagesById.containsKey(room.getLastMessageId())) {
                ChatMessage lastMsg = messagesById.get(room.getLastMessageId());
                String senderName = lastMsg.getSenderId().equals(otherId) ? otherName : "You";
                lastMsgResponse = toResponse(lastMsg, senderName);
            }

            result.add(ChatRoomResponse.builder()
                    .id(room.getId())
                    .otherUserId(otherId)
                    .otherUserName(otherName)
                    .otherUserEmail(otherEmail)
                    .otherUserRole(otherRole)
                    .avatarUrl(avatarUrl)
                    .isAssigned(false)
                    .lastMessage(lastMsgResponse)
                    .build());
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatRoomResponse> getChatContacts(Long userId, String targetRole) {
        User currentUser = userRepo.findById(userId).orElse(null);
        String currentRole = currentUser != null && currentUser.getRole() != null
                ? currentUser.getRole().getName()
                : "MEMBER";

        String partnerRole;
        if (targetRole != null && !targetRole.isBlank()) {
            partnerRole = targetRole.trim().toUpperCase();
        } else if ("TRAINER".equalsIgnoreCase(currentRole)) {
            partnerRole = "MEMBER";
        } else {
            partnerRole = "TRAINER";
        }

        // Find assigned relationships (member's booked trainers or trainer's booked members)
        Set<Long> assignedPartnerIds = new HashSet<>();
        try {
            if ("TRAINER".equalsIgnoreCase(partnerRole)) {
                List<Long> trainerIds = bookPackageRepository.findDistinctTrainerIdsByMember(userId);
                if (trainerIds != null) assignedPartnerIds.addAll(trainerIds);
            } else if ("MEMBER".equalsIgnoreCase(partnerRole)) {
                List<Long> memberIds = bookPackageRepository.findDistinctMemberIdsByTrainer(userId);
                if (memberIds != null) assignedPartnerIds.addAll(memberIds);
            }
        } catch (Exception ignored) {
        }

        // Fetch existing chat rooms for this user
        List<ChatRoom> rooms = roomRepo.findByUserId(userId);
        Map<Long, ChatRoom> roomByOtherUser = new LinkedHashMap<>();
        Set<Long> lastMsgIds = new HashSet<>();
        for (ChatRoom room : rooms) {
            Long otherId = room.getUser1Id().equals(userId) ? room.getUser2Id() : room.getUser1Id();
            if (otherId != null && !otherId.equals(userId) && !roomByOtherUser.containsKey(otherId)) {
                roomByOtherUser.put(otherId, room);
                if (room.getLastMessageId() != null) {
                    lastMsgIds.add(room.getLastMessageId());
                }
            }
        }

        // Fetch all active users with the target role
        List<User> rolePartners = userRepo.findAllActiveByRoleWithProfile(partnerRole, Status.ACTIVE);
        Map<Long, User> partnersMap = new LinkedHashMap<>();
        for (User u : rolePartners) {
            if (!u.getId().equals(userId)) {
                partnersMap.put(u.getId(), u);
            }
        }

        // Also include any user who has an existing chat room with userId even if not in rolePartners
        Set<Long> missingRoomUserIds = new HashSet<>(roomByOtherUser.keySet());
        missingRoomUserIds.removeAll(partnersMap.keySet());
        if (!missingRoomUserIds.isEmpty()) {
            for (User u : userRepo.findAllByIdInWithProfile(missingRoomUserIds)) {
                if (!u.getId().equals(userId)) {
                    partnersMap.put(u.getId(), u);
                }
            }
        }

        Map<Long, ChatMessage> messagesById = lastMsgIds.isEmpty()
                ? Collections.emptyMap()
                : messageRepo.findAllById(lastMsgIds)
                        .stream()
                        .collect(Collectors.toMap(ChatMessage::getId, Function.identity(), (a, b) -> a));

        List<ChatRoomResponse> contacts = new ArrayList<>();
        for (User partner : partnersMap.values()) {
            Long otherId = partner.getId();
            ChatRoom room = roomByOtherUser.get(otherId);

            String name = extractUserName(partner);
            String email = partner.getEmail();
            String role = partner.getRole() != null ? partner.getRole().getName() : partnerRole;
            String avatarUrl = partner.getProfile() != null ? partner.getProfile().getProfilePic() : null;
            boolean isAssigned = assignedPartnerIds.contains(otherId);

            ChatMessageResponse lastMsgResponse = null;
            if (room != null && room.getLastMessageId() != null && messagesById.containsKey(room.getLastMessageId())) {
                ChatMessage lastMsg = messagesById.get(room.getLastMessageId());
                String senderName = lastMsg.getSenderId().equals(otherId) ? name : "You";
                lastMsgResponse = toResponse(lastMsg, senderName);
            }

            contacts.add(ChatRoomResponse.builder()
                    .id(room != null ? room.getId() : otherId)
                    .otherUserId(otherId)
                    .otherUserName(name)
                    .otherUserEmail(email)
                    .otherUserRole(role)
                    .avatarUrl(avatarUrl)
                    .isAssigned(isAssigned)
                    .lastMessage(lastMsgResponse)
                    .build());
        }

        // Sort:
        // 1. Rooms with messages first (most recent message at the top)
        // 2. Assigned trainers / assigned members next
        // 3. Real accounts (non-@demo.gym.test) next, newest first (id DESC)
        // 4. Demo accounts next (id ASC)
        contacts.sort((a, b) -> {
            boolean aHasMsg = a.getLastMessage() != null;
            boolean bHasMsg = b.getLastMessage() != null;
            if (aHasMsg != bHasMsg) {
                return aHasMsg ? -1 : 1;
            }
            if (aHasMsg) {
                LocalDateTime aTime = a.getLastMessage().getCreatedAt();
                LocalDateTime bTime = b.getLastMessage().getCreatedAt();
                if (aTime != null && bTime != null) {
                    int cmp = bTime.compareTo(aTime);
                    if (cmp != 0) return cmp;
                }
            }

            boolean aAssigned = Boolean.TRUE.equals(a.getIsAssigned());
            boolean bAssigned = Boolean.TRUE.equals(b.getIsAssigned());
            if (aAssigned != bAssigned) {
                return aAssigned ? -1 : 1;
            }

            boolean aDemo = a.getOtherUserEmail() != null && a.getOtherUserEmail().endsWith("@demo.gym.test");
            boolean bDemo = b.getOtherUserEmail() != null && b.getOtherUserEmail().endsWith("@demo.gym.test");
            if (aDemo != bDemo) {
                return aDemo ? 1 : -1;
            }

            if (!aDemo) {
                return Long.compare(b.getOtherUserId(), a.getOtherUserId());
            }
            return Long.compare(a.getOtherUserId(), b.getOtherUserId());
        });

        return contacts;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getHistory(Long userId, Long otherUserId) {
        List<ChatMessage> messages = messageRepo.findPrivateMessages(userId, otherUserId);
        if (messages.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, String> namesById = userRepo.findAllByIdInWithProfile(List.of(userId, otherUserId))
                .stream()
                .collect(Collectors.toMap(User::getId, this::extractUserName, (a, b) -> a));

        return messages.stream()
                .map(m -> toResponse(m, namesById.get(m.getSenderId())))
                .collect(Collectors.toList());
    }

    private String extractUserName(User user) {
        if (user == null) {
            return "Unknown User";
        }
        Profile profile = user.getProfile();
        if (profile != null && profile.getName() != null && !profile.getName().isBlank()) {
            return profile.getName();
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            return user.getEmail().split("@")[0];
        }
        return "User #" + user.getId();
    }

    private ChatMessageResponse toResponse(ChatMessage message, String senderName) {
        return ChatMessageResponse.builder()
                .id(message.getId())
                .senderId(message.getSenderId())
                .senderName(senderName)
                .recipientId(message.getRecipientId())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .build();
    }

}
