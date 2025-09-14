package com.company.auth.service;

import com.company.auth.dto.UserEvent;
import com.company.auth.entity.User;
import com.company.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

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
        logger.info("Creating new user: {}", user.getUsername());

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        sendUserEvent("CREATED", savedUser);
        logger.info("User created successfully: {}", savedUser.getUsername());

        return savedUser;
    }

    public Optional<User> getUserById(Long id) {
        logger.debug("Fetching user by ID: {}", id);
        return userRepository.findById(id);
    }

    public List<User> getAllUsers() {
        logger.debug("Fetching all users");
        return userRepository.findAll();
    }

    public Optional<User> findByUsername(String username) {
        logger.debug("Fetching user by username: {}", username);
        return userRepository.findByUsername(username);
    }

    public User updateUser(Long id, User userDetails) {
        logger.info("Updating user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Проверяем уникальность email, если он изменен
        if (!user.getEmail().equals(userDetails.getEmail()) &&
            userRepository.existsByEmail(userDetails.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        user.setEmail(userDetails.getEmail());
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());

        // Если передан новый пароль, хешируем его
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }

        User updatedUser = userRepository.save(user);
        sendUserEvent("UPDATED", updatedUser);
        logger.info("User updated successfully: {}", updatedUser.getUsername());

        return updatedUser;
    }

    public void deleteUser(Long id) {
        logger.info("Deleting user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        userRepository.deleteById(id);
        sendUserEvent("DELETED", user);
        logger.info("User deleted successfully: {}", user.getUsername());
    }

    private void sendUserEvent(String eventType, User user) {
        try {
            UserEvent event = new UserEvent();
            event.setEventType(eventType);
            event.setUsername(user.getUsername());
            event.setPassword(user.getPassword()); // В реальном проекте не отправляйте пароль!
            event.setEmail(user.getEmail());
            event.setRole(user.getRole().name());

            kafkaTemplate.send("user-events", event);
            logger.debug("Sent {} event for user: {}", eventType, user.getUsername());
        } catch (Exception e) {
            logger.error("Failed to send user event to Kafka: {}", e.getMessage());
        }
    }
}