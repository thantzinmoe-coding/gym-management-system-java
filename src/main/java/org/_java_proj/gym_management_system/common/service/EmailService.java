package org._java_proj.gym_management_system.common.service;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
}
