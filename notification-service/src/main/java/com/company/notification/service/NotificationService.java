package com.company.notification.service;

import com.company.notification.dto.UserEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final EmailService emailService;
    private final List<String> adminEmails = List.of("admin@company.com"); // В реальном проекте получать из БД

    public NotificationService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void handleUserEvent(UserEvent event) {
        logger.info("Processing user event: {} for user: {}", event.getEventType(), event.getUsername());

        // Отправляем уведомление только если пользователь USER
        if ("USER".equals(event.getRole())) {
            String subject = getSubject(event.getEventType(), event.getUsername());
            String text = getMessageText(event.getEventType(), event.getUsername(),
                                       event.getPassword(), event.getEmail());

            // Отправляем email всем админам
            for (String adminEmail : adminEmails) {
                try {
                    emailService.sendEmail(adminEmail, subject, text);
                    logger.info("Notification sent to admin: {} about user: {}", adminEmail, event.getUsername());
                } catch (Exception e) {
                    logger.error("Failed to send email to {}: {}", adminEmail, e.getMessage());
                }
            }
        } else {
            logger.debug("Skipping notification for {} user: {}", event.getRole(), event.getUsername());
        }
    }

    private String getSubject(String eventType, String username) {
        switch (eventType.toUpperCase()) {
            case "CREATED": return "Создан пользователь " + username;
            case "UPDATED": return "Изменен пользователь " + username;
            case "DELETED": return "Удален пользователь " + username;
            default: return "Действие с пользователем " + username;
        }
    }

    private String getMessageText(String eventType, String username,
                                String password, String email) {
        String action = getActionText(eventType);
        return action + " пользователь с именем - " + username +
               ", паролем - " + password + " и почтой - " + email + ".";
    }

    private String getActionText(String eventType) {
        switch (eventType.toUpperCase()) {
            case "CREATED": return "Создан";
            case "UPDATED": return "Изменен";
            case "DELETED": return "Удален";
            default: return "Изменен";
        }
    }
}