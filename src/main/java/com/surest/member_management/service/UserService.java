package com.surest.member_management.service;

import com.surest.member_management.entity.User;
import com.surest.member_management.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {


    private final UserRepository userRepository;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }


    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }


    public User save(User user) {
        return userRepository.save(user);
    }


    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}