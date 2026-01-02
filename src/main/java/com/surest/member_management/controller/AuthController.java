package com.surest.member_management.controller;


import com.surest.member_management.config.JwtUtil;
import com.surest.member_management.dto.LoginRequestDto;
import com.surest.member_management.dto.LoginResponseDto;
import com.surest.member_management.entity.User;
import com.surest.member_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;


    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @RequestBody LoginRequestDto request) {
        System.out.println("AUTH CONTROLLER HIT");

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User users = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow();

        String token = jwtUtil.generateToken(
                users.getUsername(),
                users.getRole().getName());

        return ResponseEntity.ok(new LoginResponseDto(token));
    }
}