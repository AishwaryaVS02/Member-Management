package com.surest.member_management.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private final Environment environment;

    private SecretKey signingKey;
    private long expirationTime;

    public JwtUtil(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void init() {
        String secret = environment.getProperty("jwt.secret");
        String expiration = environment.getProperty("jwt.expiration");

        if (secret == null || expiration == null) {
            throw new IllegalStateException(
                    "JWT configuration missing. Please define jwt.secret and jwt.expiration in application.yml"
            );
        }

        this.expirationTime = Long.parseLong(expiration);
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // ✅ Generate JWT token
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(signingKey)
                .compact();
    }

    // ✅ Extract username from token
    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    // ✅ Validate token
    public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}