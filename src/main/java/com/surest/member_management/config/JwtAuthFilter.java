package com.surest.member_management.config;


import com.surest.member_management.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthFilter(JwtUtil jwtUtil,
                         CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // ✅ 1. BYPASS AUTH ENDPOINT (CRITICAL FIX)
        if (request.getServletPath().equals("/auth/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ 2. READ AUTH HEADER
        String authHeader = request.getHeader("Authorization");

        // ✅ 3. IF NO TOKEN → CONTINUE (DO NOT BLOCK)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ 4. EXTRACT TOKEN
        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);

        // ✅ 5. VALIDATE TOKEN & AUTHENTICATE USER
        if (username != null &&
                SecurityContextHolder.getContext().getAuthentication() == null &&
                jwtUtil.isTokenValid(token)) {
            System.out.println("JWT FILTER HIT");

            UserDetails userDetails =
                    userDetailsService.loadUserByUsername(username);

            System.out.println("Username: " + username);
            System.out.println("Authorities: " + userDetails.getAuthorities());

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request)
            );

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);
        }

        // ✅ 6. CONTINUE FILTER CHAIN
        filterChain.doFilter(request, response);
    }
}