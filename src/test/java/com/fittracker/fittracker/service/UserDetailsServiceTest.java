package com.fittracker.fittracker.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.fittracker.fittracker.entity.User;
import com.fittracker.fittracker.entity.UserDetails;
import com.fittracker.fittracker.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

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
            User user = new User(UUID.fromString("948cc727-68e5-455c-ab6d-942e585bde0d"), "user", "user@example.com",
                "password");
            when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

            var expected = UserDetails.fromUser(user);
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