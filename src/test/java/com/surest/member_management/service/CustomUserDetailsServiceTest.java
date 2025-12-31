package com.surest.member_management.service;

import com.surest.member_management.entity.User;
import com.surest.member_management.entity.Role;
import com.surest.member_management.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    //SUCCESS CASE
    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenUserExists() {

        User user = new User();
        user.setUsername("admin");
        user.setPasswordHash("encoded-password");

        Role role = new Role();
        role.setName("ROLE_ADMIN");
        user.setRole(role);

        when(userRepository.findByUsername("admin"))
                .thenReturn(Optional.of(user));


        UserDetails userDetails =
                customUserDetailsService.loadUserByUsername("admin");


        assertNotNull(userDetails);
        assertEquals("admin", userDetails.getUsername());
        assertEquals("encoded-password", userDetails.getPassword());

        assertTrue(
                userDetails.getAuthorities()
                        .stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
        );

        verify(userRepository, times(1)).findByUsername("admin");
    }

    //FAILURE CASE
    @Test
    void loadUserByUsername_shouldThrowException_whenUserNotFound() {

        when(userRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());


        assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("unknown")
        );

        verify(userRepository, times(1)).findByUsername("unknown");
    }
}
