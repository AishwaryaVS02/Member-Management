package com.surest.member_management.service;

import com.surest.member_management.entity.User;
import com.surest.member_management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        user = new User();
        user.setId(userId);
        user.setUsername("admin");
        user.setPasswordHash("encoded-password");
    }


    @Test
    void findByUsername_shouldReturnUser_whenUserExists() {
        when(userRepository.findByUsername("admin"))
                .thenReturn(Optional.of(user));

        Optional<User> result = userService.findByUsername("admin");

        assertTrue(result.isPresent());
        assertEquals("admin", result.get().getUsername());
        verify(userRepository).findByUsername("admin");
    }

    @Test
    void findByUsername_shouldReturnEmpty_whenUserNotFound() {
        when(userRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        Optional<User> result = userService.findByUsername("unknown");

        assertTrue(result.isEmpty());
        verify(userRepository).findByUsername("unknown");
    }


    @Test
    void findById_shouldReturnUser_whenUserExists() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        Optional<User> result = userService.findById(userId);

        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getId());
        verify(userRepository).findById(userId);
    }

    @Test
    void findById_shouldReturnEmpty_whenUserNotFound() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        Optional<User> result = userService.findById(userId);

        assertTrue(result.isEmpty());
        verify(userRepository).findById(userId);
    }


    @Test
    void save_shouldPersistUser() {
        when(userRepository.save(user))
                .thenReturn(user);

        User savedUser = userService.save(user);

        assertNotNull(savedUser);
        assertEquals("admin", savedUser.getUsername());
        verify(userRepository).save(user);
    }


    @Test
    void existsByUsername_shouldReturnTrue_whenUserExists() {
        when(userRepository.findByUsername("admin"))
                .thenReturn(Optional.of(user));

        boolean exists = userService.existsByUsername("admin");

        assertTrue(exists);
        verify(userRepository).findByUsername("admin");
    }

    @Test
    void existsByUsername_shouldReturnFalse_whenUserDoesNotExist() {
        when(userRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        boolean exists = userService.existsByUsername("unknown");

        assertFalse(exists);
        verify(userRepository).findByUsername("unknown");
    }
}


