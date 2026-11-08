package org.java_proj.gym_management_system.common.listener;

import lombok.extern.slf4j.Slf4j;
import org.java_proj.gym_management_system.common.event.EmailEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class EmailEventListener {

    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://api.brevo.com/v3")
            .build();

    @Value("${brevo.api-key}")
    private String brevoApiKey;

    @Value("${brevo.sender-email}")
    private String senderEmail;

    @Value("${brevo.sender-name:Gym Management System}")
    private String senderName;

    @Async
    @EventListener
    public void handleEmailEvent(EmailEvent event) {
        try {
            // Strip any <img ... src="cid:logoImage" ...> tag from the HTML template
            String htmlContent = event.getBody() != null
                    ? event.getBody().replaceAll("(?i)<img[^>]*cid:logoImage[^>]*>", "")
                    : "";

            Map<String, Object> payload = Map.of(
                    "sender", Map.of("name", senderName, "email", senderEmail),
                    "to", List.of(Map.of("email", event.getToEmail())),
                    "subject", event.getSubject(),
                    "htmlContent", htmlContent
            );

            String response = restClient.post()
                    .uri("/smtp/email")
                    .header("api-key", brevoApiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .body(String.class);

            log.info("Successfully sent email via Brevo to {}: {}", event.getToEmail(), response);
        } catch (Exception e) {
            log.error("Failed to send MIME email to {}", event.getToEmail(), e);
        }
    }
}