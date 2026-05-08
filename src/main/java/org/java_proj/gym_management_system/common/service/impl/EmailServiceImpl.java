package org.java_proj.gym_management_system.common.service.impl;

import org.java_proj.gym_management_system.common.event.EmailEvent;
import org.java_proj.gym_management_system.common.service.EmailService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final ApplicationEventPublisher eventPublisher;

    public EmailServiceImpl(final ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void sendEmail(final String toEmail, final String subject, final String body) {
        this.eventPublisher.publishEvent(new EmailEvent(this, toEmail, subject, body));
    }
}
