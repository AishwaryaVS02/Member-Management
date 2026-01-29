package com.surest.member_management.controller;

import com.surest.member_management.config.JwtUtil;
import com.surest.member_management.dto.LoginRequestDto;
import com.surest.member_management.dto.LoginResponseDto;
import com.surest.member_management.entity.Role;
import com.surest.member_management.entity.User;
import com.surest.member_management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_successfulAuthentication_returnsToken() {
        // Arrange
        LoginRequestDto request = new LoginRequestDto();
        request.setUsername("john");
        request.setPassword("password123");

        Role role = new Role();
        role.setName("USER");

        User user = new User();
        user.setUsername("john");
        user.setRole(role);

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken("john", "USER")).thenReturn("mocked-jwt-token");

        // Act
        ResponseEntity<LoginResponseDto> response = authController.login(request);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("mocked-jwt-token", response.getBody().getToken());

        // Verify authenticationManager was called correctly
        ArgumentCaptor<UsernamePasswordAuthenticationToken> authCaptor = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(authCaptor.capture());
        assertEquals("john", authCaptor.getValue().getPrincipal());
        assertEquals("password123", authCaptor.getValue().getCredentials());

        // Verify repository and jwt were called
        verify(userRepository).findByUsername("john");
        verify(jwtUtil).generateToken("john", "USER");
    }

    @Test
    void login_userNotFound_throwsException() {
        // Arrange
        LoginRequestDto request = new LoginRequestDto();
        request.setUsername("john");
        request.setPassword("password123");

        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> authController.login(request));

        // Verify authenticate was called
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByUsername("john");
        verifyNoInteractions(jwtUtil);
    }
}
