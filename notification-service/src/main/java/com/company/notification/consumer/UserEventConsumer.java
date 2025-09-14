package com.company.notification.consumer;

import com.company.notification.dto.UserEvent;
import com.company.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(UserEventConsumer.class);

    private final NotificationService notificationService;

    public UserEventConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "user-events", groupId = "notification-group")
    public void consumeUserEvent(UserEvent event) {
        try {
            logger.info("Received user event: {} for user: {}",
                       event.getEventType(), event.getUsername());

            notificationService.handleUserEvent(event);
            logger.debug("Successfully processed event for user: {}", event.getUsername());

        } catch (Exception e) {
            logger.error("Error processing user event: {}", e.getMessage());
        }
    }
}