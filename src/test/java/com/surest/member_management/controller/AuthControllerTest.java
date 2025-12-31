package com.surest.member_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.surest.member_management.config.JwtUtil;
import com.surest.member_management.dto.LoginRequestDto;
import com.surest.member_management.entity.Role;
import com.surest.member_management.entity.User;
import com.surest.member_management.repository.UserRepository;
import com.surest.member_management.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationManager authenticationManager;
    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void login_shouldReturnJwtToken_whenCredentialsAreValid() throws Exception {


        LoginRequestDto request = new LoginRequestDto();
        request.setUsername("admin");
        request.setPassword("password");

        Role role = new Role();
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setUsername("admin");
        user.setPasswordHash("encoded-password");
        user.setRole(role);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);

        when(userRepository.findByUsername("admin"))
                .thenReturn(Optional.of(user));

        when(jwtUtil.generateToken("admin", "ROLE_ADMIN"))
                .thenReturn("mock-jwt-token");


        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"));

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        verify(userRepository, times(1))
                .findByUsername("admin");

        verify(jwtUtil, times(1))
                .generateToken("admin", "ROLE_ADMIN");
    }
}
