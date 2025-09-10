package org._java_proj.gym_management_system.features.message.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org._java_proj.gym_management_system.features.message.repository.OnlineUserRepository;
import org._java_proj.gym_management_system.features.message.service.OnlineUserService;
import org._java_proj.gym_management_system.model.OnlineUser;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OnlineUserServiceImpl implements OnlineUserService {
    private final OnlineUserRepository onlineUserRepository;

    @Transactional
    public void setUserOnline(Long userId, String sessionId) {
        Optional<OnlineUser> existingUser = onlineUserRepository.findByUserId(userId);

        if (existingUser.isPresent()) {
            OnlineUser user = existingUser.get();
            user.setSessionId(sessionId);
            user.setLastSeen(LocalDateTime.now());
            user.setIsActive(true);
            onlineUserRepository.save(user);
        } else {
            OnlineUser newUser = OnlineUser.builder()
                    .userId(userId)
                    .sessionId(sessionId)
                    .lastSeen(LocalDateTime.now())
                    .isActive(true)
                    .build();
            onlineUserRepository.save(newUser);
        }

        log.debug("User {} set online with session {}", userId, sessionId);
    }

    @Transactional
    public void setUserOffline(Long userId, String sessionId) {
        Optional<OnlineUser> user = onlineUserRepository.findByUserIdAndSessionId(userId, sessionId);
        user.ifPresent(onlineUser -> {
            onlineUser.setIsActive(false);
            onlineUser.setLastSeen(LocalDateTime.now());
            onlineUserRepository.save(onlineUser);
        });

        log.debug("User {} set offline for session {}", userId, sessionId);
    }

    public boolean isUserOnline(Long userId) {
        Optional<OnlineUser> user = onlineUserRepository.findByUserId(userId);
        return user.map(OnlineUser::getIsActive).orElse(false);
    }

    public List<Long> getOnlineUsers(List<Long> userIds) {
        return onlineUserRepository.findActiveUsersByIdIn(userIds)
                .stream()
                .map(OnlineUser::getUserId)
                .toList();
    }

    @Scheduled(fixedRate = 300000)
    @Transactional
    public void cleanupStaleUsers() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(10);
        onlineUserRepository.deactivateStaleUsers(cutoff);
        log.debug("Cleaned up stale online user records");
    }

    @Override
    public Optional<OnlineUser> getOnlineUserBySessionId(String sessionId) {
        return onlineUserRepository.findBySessionId(sessionId);
    }

}