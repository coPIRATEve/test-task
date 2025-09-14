package com.company.notification.service;

import com.company.notification.dto.UserEvent;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final EmailService emailService;

    public NotificationService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void handleUserEvent(UserEvent event) {
        if ("USER".equals(event.getRole())) {
            String subject = getSubject(event.getEventType(), event.getUsername());
            String text = getMessageText(event.getEventType(), event.getUsername(),
                                       event.getPassword(), event.getEmail());

            String adminEmail = "admin@company.com";

            emailService.sendEmail(adminEmail, subject, text);
        }
    }

    private String getSubject(String eventType, String username) {
        switch (eventType) {
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
        switch (eventType) {
            case "CREATED": return "Создан";
            case "UPDATED": return "Изменен";
            case "DELETED": return "Удален";
            default: return "Изменен";
        }
    }
}