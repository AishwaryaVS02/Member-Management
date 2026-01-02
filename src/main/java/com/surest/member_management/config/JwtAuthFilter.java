package com.surest.member_management.config;


import com.surest.member_management.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getServletPath().startsWith("/auth");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("JWT FILTER HIT");

        String header = request.getHeader("Authorization");
        System.out.println("Authorization Header = " + header);

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            System.out.println("JWT Token = " + token);

            String username = jwtUtil.extractUsername(token);
            System.out.println("Extracted username = " + username);

            UserDetails userDetails =
                    userDetailsService.loadUserByUsername(username);

            System.out.println("UserDetails authorities = "
                    + userDetails.getAuthorities());


            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);


            System.out.println("SecurityContext authorities = "
                    + SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getAuthorities());
        }

        filterChain.doFilter(request, response);
    }
}