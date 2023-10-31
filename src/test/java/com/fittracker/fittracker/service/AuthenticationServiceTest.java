package com.fittracker.fittracker.service;

import com.fittracker.fittracker.entity.User;
import com.fittracker.fittracker.exception.UserAlreadyExistsException;
import com.fittracker.fittracker.repository.UserRepository;
import com.fittracker.fittracker.request.RegisterRequest;
import com.fittracker.fittracker.response.RegisterResponse;
import com.fittracker.fittracker.security.JwtUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testcontainers.shaded.com.trilead.ssh2.auth.AuthenticationManager;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    private static final RegisterRequest registerRequest = new RegisterRequest("user", "user@example.com", "password");

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    UserRepository userRepository;
    @Mock
    JwtUtils jwtUtils;
    @Mock
    AuthenticationManager authenticationManager;

    @InjectMocks
    AuthenticationService authenticationService;

    @Nested
    class Register {
        @Test
        void givenNonExistingUser_shouldReturnRegisterResponse() {
            UUID uuid = UUID.randomUUID();

            when(userRepository.existsByUsernameOrEmail(any(), any())).thenReturn(false);
            when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
            when(userRepository.save(any())).thenReturn(new User(uuid, "user", "user@example.com", "password"));

            var expected = new RegisterResponse(uuid, "user", "user@example.com");
            var result = authenticationService.register(registerRequest);

            assertThat(result).isEqualTo(expected);
            verify(userRepository).save(userCaptor.capture());
            assertThat(userCaptor.getValue()).usingRecursiveComparison().isEqualTo(new User(null, "user", "user@example.com", "encodedPassword"));
            verifyNoMoreInteractions(userRepository);
        }

        @Test
        void givenExistingUser_shouldThrowException() {
            when(userRepository.existsByUsernameOrEmail(any(), any())).thenReturn(true);

            try {
                authenticationService.register(registerRequest);
            } catch (Exception e) {
                assertThat(e).isInstanceOf(UserAlreadyExistsException.class)
                        .hasMessageContaining("User already exists for username/email provided: user/user@example.com");
            }

            verify(userRepository).existsByUsernameOrEmail(any(), any());
            verifyNoMoreInteractions(userRepository);
        }

    }

    @Nested
    class Login {

    }

}

