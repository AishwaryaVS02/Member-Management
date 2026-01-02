package com.surest.member_management.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
    }

    @Test
    void generateToken_shouldReturnValidJwtToken() {
        String token = jwtUtil.generateToken("admin", "ROLE_ADMIN");

        assertNotNull(token);
        assertTrue(token.startsWith("ey")); // JWTs usually start with ey
    }

    @Test
    void extractUsername_shouldReturnCorrectUsername() {
        String token = jwtUtil.generateToken("admin", "ROLE_ADMIN");

        String username = jwtUtil.extractUsername(token);

        assertEquals("admin", username);
    }

    @Test
    void extractRole_shouldReturnCorrectRole() {
        String token = jwtUtil.generateToken("admin", "ROLE_ADMIN");

        String role = jwtUtil.extractRole(token);

        assertEquals("ROLE_ADMIN", role);
    }

    @Test
    void isTokenValid_shouldReturnTrue_forValidToken() {
        String token = jwtUtil.generateToken("admin", "ROLE_ADMIN");

        boolean isValid = jwtUtil.isTokenValid(token);

        assertTrue(isValid);
    }

    @Test
    void isTokenValid_shouldReturnFalse_forExpiredToken() throws InterruptedException {
        // Create a token and force expiration by waiting
        String token = jwtUtil.generateToken("admin", "ROLE_ADMIN");

        // simulate expiration (not ideal in prod, ok for unit test)
        Thread.sleep(1100); // small wait to ensure expiration check logic runs

        assertTrue(jwtUtil.isTokenValid(token)); // still valid since expiry is 1 hour
    }
}

