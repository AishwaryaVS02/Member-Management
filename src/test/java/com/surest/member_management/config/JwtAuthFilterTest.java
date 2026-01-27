package com.surest.member_management.config;

import com.surest.member_management.service.CustomUserDetailsService;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;


import jakarta.servlet.FilterChain;


import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class JwtAuthFilterTest {

    private JwtUtil jwtUtil;
    private CustomUserDetailsService userDetailsService;
    private JwtAuthFilter filter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        jwtUtil = mock(JwtUtil.class);
        userDetailsService = mock(CustomUserDetailsService.class);
        filter = new JwtAuthFilter(jwtUtil, userDetailsService);

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = mock(FilterChain.class);

        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldNotFilter_authPath_returnsTrue() {
        request.setServletPath("/api/v1/auth/login");
        assertThat(filter.shouldNotFilter(request)).isTrue();
    }

    @Test
    void shouldNotFilter_otherPath_returnsFalse() {
        request.setServletPath("/api/v1/members");
        assertThat(filter.shouldNotFilter(request)).isFalse();
    }

    @Test
    void doFilterInternal_noAuthorizationHeader_callsChain() throws Exception {
        filter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_headerDoesNotStartWithBearer_callsChain() throws Exception {
        request.addHeader("Authorization", "Token abc");
        filter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_invalidToken_throwsBadCredentialsException() throws Exception {
        request.addHeader("Authorization", "Bearer invalidToken");
        when(jwtUtil.extractUsername("invalidToken")).thenReturn("user");
        when(jwtUtil.isTokenValid("invalidToken")).thenReturn(false);
        when(userDetailsService.loadUserByUsername("user"))
                .thenReturn(User.withUsername("user").password("pass").authorities(Collections.emptyList()).build());

        assertThatThrownBy(() -> filter.doFilterInternal(request, response, filterChain))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Invalid JWT token");

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_validToken_setsAuthentication() throws Exception {
        request.addHeader("Authorization", "Bearer validToken");

        when(jwtUtil.extractUsername("validToken")).thenReturn("user");
        when(jwtUtil.isTokenValid("validToken")).thenReturn(true);

        UserDetails userDetails = User.withUsername("user")
                .password("pass")
                .authorities(Collections.emptyList())
                .build();
        when(userDetailsService.loadUserByUsername("user")).thenReturn(userDetails);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getName()).isEqualTo("user");
    }

    @Test
    void doFilterInternal_extractUsernameReturnsNull_throwsBadCredentialsException() {
        request.addHeader("Authorization", "Bearer tokenWithoutUsername");
        when(jwtUtil.extractUsername("tokenWithoutUsername")).thenReturn(null);

        assertThatThrownBy(() -> filter.doFilterInternal(request, response, filterChain))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("JWT token does not contain username");

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_jwtThrowsJwtException_throwsBadCredentialsException() {
        request.addHeader("Authorization", "Bearer someToken");
        when(jwtUtil.extractUsername("someToken")).thenThrow(new JwtException("jwt error"));

        assertThatThrownBy(() -> filter.doFilterInternal(request, response, filterChain))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Invalid JWT token");

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_anyOtherException_clearsContextAndRethrows() {
        request.addHeader("Authorization", "Bearer anyToken");
        when(jwtUtil.extractUsername("anyToken")).thenThrow(new RuntimeException("oops"));

        assertThatThrownBy(() -> filter.doFilterInternal(request, response, filterChain))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("oops");

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
