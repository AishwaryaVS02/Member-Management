package com.surest.member_management.service;

import com.surest.member_management.entity.User;
import com.surest.member_management.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    // Constructor injection (BEST PRACTICE)
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ✅ Find user by username (used by auth)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // ✅ Find user by ID (future use)
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    // ✅ Save user (registration / admin creation)
    public User save(User user) {
        return userRepository.save(user);
    }

    // ✅ Check if username exists
    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}