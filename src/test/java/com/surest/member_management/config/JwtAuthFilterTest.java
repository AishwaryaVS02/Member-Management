package com.surest.member_management.config;

import com.surest.member_management.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;

    @BeforeEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    // 🔹 CASE 1: Valid JWT → Authentication should be set
    @Test
    void doFilterInternal_shouldAuthenticateUser_whenValidJwtProvided() throws Exception {

        String token = "valid.jwt.token";
        String username = "admin";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/members");
        request.addHeader("Authorization", "Bearer " + token);

        MockHttpServletResponse response = new MockHttpServletResponse();

        UserDetails userDetails = new User(
                "admin",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username))
                .thenReturn(userDetails);

        // when
        jwtAuthFilter.doFilter(request, response, filterChain);

        // then
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(
                "admin",
                SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getName()
        );

        verify(filterChain).doFilter(request, response);
    }

    // 🔹 CASE 2: No Authorization header → Authentication NOT set
    @Test
    void doFilterInternal_shouldNotAuthenticate_whenNoAuthorizationHeader() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/members");

        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtAuthFilter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verify(filterChain).doFilter(request, response);
    }

    // 🔹 CASE 3: /auth endpoint → filter should be skipped
    @Test
    void shouldNotFilter_shouldReturnTrue_forAuthEndpoint() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/auth/login");

        boolean result = jwtAuthFilter.shouldNotFilter(request);

        assertTrue(result);
    }
}
