package com.fittracker.fittracker.service;

import com.fittracker.fittracker.entity.User;
import com.fittracker.fittracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserDetailsService userDetailsService;
    @Test
    void givenUsernameOfExistingUser_shouldReturnUser() {
        User user = new User(UUID.randomUUID(),"user","user@example.com","password");
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        var expected = new org.springframework.security.core.userdetails.User(
                "user", "password", List.of());
        var result = userDetailsService.loadUserByUsername("user");

        assertThat(result).isEqualTo(expected);
        verify(userRepository).findByUsername("user");
        verifyNoMoreInteractions(userRepository);
    }

}