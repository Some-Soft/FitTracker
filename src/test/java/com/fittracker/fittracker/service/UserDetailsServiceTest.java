package com.fittracker.fittracker.service;

import com.fittracker.fittracker.entity.User;
import com.fittracker.fittracker.repository.UserRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsService userDetailsService;

    @Nested
    class LoadByUsername {
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

        @Test
        void givenUsernameOfNonexistentUser_shouldThrowException() {
            when(userRepository.findByUsername(any())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userDetailsService.loadUserByUsername("user"))
                    .isInstanceOf(UsernameNotFoundException.class)
                    .hasMessageContaining("Username user not found");

            verify(userRepository).findByUsername("user");
            verifyNoMoreInteractions(userRepository);
        }
    }


}