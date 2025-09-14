package com.company.auth.service;

import com.company.auth.dto.UserEvent;
import com.company.auth.entity.User;
import com.company.auth.repository.UserRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    public UserService(UserRepository userRepository,
                      PasswordEncoder passwordEncoder,
                      KafkaTemplate<String, UserEvent> kafkaTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.kafkaTemplate = kafkaTemplate;
    }


    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        // Отправляем событие в Kafka
        sendUserEvent("CREATED", savedUser);
        return savedUser;
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEmail(userDetails.getEmail());
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());

        User updatedUser = userRepository.save(user);
        sendUserEvent("UPDATED", updatedUser);
        return updatedUser;
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userRepository.deleteById(id);
        sendUserEvent("DELETED", user);
    }

    private void sendUserEvent(String eventType, User user) {
        UserEvent event = new UserEvent();
        event.setEventType(eventType);
        event.setUsername(user.getUsername());
        event.setPassword(user.getPassword());
        event.setEmail(user.getEmail());
        event.setRole(user.getRole().name());

        kafkaTemplate.send("user-events", event);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}