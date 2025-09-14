package com.company.auth.controller;

import com.company.auth.dto.LoginRequest;
import com.company.auth.entity.User;
import com.company.auth.service.JwtService;
import com.company.auth.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, JwtService jwtService,
                         PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        if (userService.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        userService.createUser(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> user = userService.findByUsername(loginRequest.getUsername());
        if (user.isPresent() && passwordEncoder.matches(loginRequest.getPassword(),
                user.get().getPassword())) {
            String token = jwtService.generateToken(loginRequest.getUsername());
            return ResponseEntity.ok(token);
        }
        return ResponseEntity.badRequest().body("Invalid credentials");
    }
}