package com.company.notification.consumer;

import com.company.notification.dto.UserEvent;
import com.company.notification.service.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserEventConsumer {

    private final NotificationService notificationService;

    public UserEventConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "user-events", groupId = "notification-group")
    public void consumeUserEvent(UserEvent event) {
        System.out.println("Received user event: " + event.getEventType() + " for " + event.getUsername());
        notificationService.handleUserEvent(event);
    }
}