package com.surest.member_management.service;

import com.surest.member_management.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserService {

    Optional<User> findByUsername(String username);

    Optional<User> findById(UUID id);

    User save(User user);

    boolean existsByUsername(String username);
}
