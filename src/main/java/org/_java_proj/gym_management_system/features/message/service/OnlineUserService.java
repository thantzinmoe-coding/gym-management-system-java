package org._java_proj.gym_management_system.features.message.service;

import java.util.List;

public interface OnlineUserService {
    void setUserOnline(Long userId, String sessionId);
    void setUserOffline(Long userId, String sessionId);
    boolean isUserOnline(Long userId);
    List<Long> getOnlineUsers(List<Long> userIds);
}
