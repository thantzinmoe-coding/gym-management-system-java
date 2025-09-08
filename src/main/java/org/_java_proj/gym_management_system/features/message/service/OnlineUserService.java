package org._java_proj.gym_management_system.features.message.service;

import org._java_proj.gym_management_system.model.OnlineUser;

import java.util.List;
import java.util.Optional;

public interface OnlineUserService {
    void setUserOnline(Long userId, String sessionId);
    void setUserOffline(Long userId, String sessionId);
    boolean isUserOnline(Long userId);
    List<Long> getOnlineUsers(List<Long> userIds);

    Optional<OnlineUser> getOnlineUserBySessionId(String sessionId);
}
