package com.company.auth.controller;

import com.company.auth.entity.User;
import com.company.auth.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id,
                                          Authentication authentication) {
        Optional<User> user = userService.getUserById(id);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // USER может видеть только свой аккаунт, ADMIN - любой
        if (!hasAccess(authentication, user.get().getUsername())) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(user.get());
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id,
                                         @RequestBody User userDetails,
                                         Authentication authentication) {
        Optional<User> existingUser = userService.getUserById(id);
        if (existingUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (!hasAccess(authentication, existingUser.get().getUsername())) {
            return ResponseEntity.status(403).build();
        }

        try {
            User updatedUser = userService.updateUser(id, userDetails);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id,
                                         Authentication authentication) {
        Optional<User> user = userService.getUserById(id);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (!hasAccess(authentication, user.get().getUsername())) {
            return ResponseEntity.status(403).build();
        }

        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private boolean hasAccess(Authentication authentication, String targetUsername) {
        String currentUsername = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        return isAdmin || currentUsername.equals(targetUsername);
    }
}