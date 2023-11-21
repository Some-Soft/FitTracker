package com.fittracker.fittracker.service;

import static com.fittracker.fittracker.dataprovider.Entity.user;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.fittracker.fittracker.entity.UserDetails;
import com.fittracker.fittracker.repository.UserRepository;
import java.util.Optional;
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

    private static final String TEST_USERNAME = "user";

    @Nested
    class LoadByUsername {

        @Test
        void givenUsernameOfExistingUser_shouldReturnUser() {
            when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(user()));

            var expected = UserDetails.fromUser(user());
            var result = userDetailsService.loadUserByUsername(TEST_USERNAME);

            assertThat(result).isEqualTo(expected);
            verify(userRepository).findByUsername(TEST_USERNAME);
            verifyNoMoreInteractions(userRepository);
        }

        @Test
        void givenUsernameOfNonexistentUser_shouldThrowException() {
            when(userRepository.findByUsername(any())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userDetailsService.loadUserByUsername(TEST_USERNAME))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Username user not found");

            verify(userRepository).findByUsername(TEST_USERNAME);
            verifyNoMoreInteractions(userRepository);
        }
    }


}