package com.company.notification.dto;

import lombok.Data;

@Data
public class UserEvent {
    private String eventType; // CREATED, UPDATED, DELETED
    private String username;
    private String password;
    private String email;
    private String role; // USER или ADMIN
}