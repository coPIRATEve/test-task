package com.company.auth.dto;

import com.company.auth.entity.User;
import lombok.Data;

@Data
public class UserRequest {
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private User.Role role;
}