package com.fittracker.fittracker.service;

import com.fittracker.fittracker.entity.User;
import com.fittracker.fittracker.exception.UserAlreadyExistsException;
import com.fittracker.fittracker.repository.UserRepository;
import com.fittracker.fittracker.request.LoginRequest;
import com.fittracker.fittracker.request.RegisterRequest;
import com.fittracker.fittracker.response.LoginResponse;
import com.fittracker.fittracker.response.RegisterResponse;
import com.fittracker.fittracker.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    private static final RegisterRequest REGISTER_REQUEST = new RegisterRequest("user", "user@example.com", "password");

    private static final UUID TEST_UUID = UUID.randomUUID();
    private static final LoginRequest LOGIN_REQUEST = new LoginRequest("user","password");

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void beforeEach() {
        lenient().when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        lenient().when(userRepository.save(any())).thenReturn(new User(TEST_UUID, "user", "user@example.com", "password"));
    }

    @Nested
    class Register {
        @Test
        void givenNonexistentUser_shouldReturnRegisterResponse() {
            when(userRepository.existsByUsernameOrEmail(any(), any())).thenReturn(false);

            var expected = new RegisterResponse(TEST_UUID, "user", "user@example.com");
            var result = authenticationService.register(REGISTER_REQUEST);

            assertThat(result).isEqualTo(expected);
            verify(userRepository).existsByUsernameOrEmail("user", "user@example.com");
            verify(passwordEncoder).encode("password");
            verify(userRepository).save(userCaptor.capture());
            assertThat(userCaptor.getValue()).usingRecursiveComparison().isEqualTo(new User(null, "user", "user@example.com", "encodedPassword"));
            verifyNoMoreInteractions(userRepository);
        }

        @Test
        void givenExistingUser_shouldThrowException() {
            when(userRepository.existsByUsernameOrEmail(any(), any())).thenReturn(true);

            assertThatThrownBy(() -> authenticationService.register(REGISTER_REQUEST))
                    .isInstanceOf(UserAlreadyExistsException.class)
                    .hasMessageContaining("User already exists for username/email provided: user/user@example.com");

            verify(userRepository).existsByUsernameOrEmail(any(), any());
            verifyNoMoreInteractions(userRepository);
            verifyNoInteractions(passwordEncoder);
        }

    }

    @Nested
    class Login {

        private static final UsernamePasswordAuthenticationToken USERNAME_PASSWORD_AUTHENTICATION_TOKEN = new UsernamePasswordAuthenticationToken("user", "password");

        @Test
        void givenValidCredentials_shouldReturnLoginResponse() {
            when(authenticationManager.authenticate(USERNAME_PASSWORD_AUTHENTICATION_TOKEN)).thenReturn(authentication);
            when(jwtUtils.generateToken(any())).thenReturn("token");

            var result = authenticationService.login(LOGIN_REQUEST);

            assertThat(result).isEqualTo(new LoginResponse("token"));
            verify(jwtUtils).generateToken(authentication);
            verify(authenticationManager).authenticate(USERNAME_PASSWORD_AUTHENTICATION_TOKEN);
            verifyNoMoreInteractions(jwtUtils);
            verifyNoMoreInteractions(authenticationManager);
        }

        @Test
        void givenInvalidCredentials_shouldThrowException() {
            when(authenticationManager.authenticate(USERNAME_PASSWORD_AUTHENTICATION_TOKEN)).thenThrow(BadCredentialsException.class);

            assertThatThrownBy(() -> authenticationService.login(LOGIN_REQUEST))
                    .isInstanceOf(BadCredentialsException.class);

            verify(authenticationManager).authenticate(USERNAME_PASSWORD_AUTHENTICATION_TOKEN);
            verifyNoMoreInteractions(authenticationManager);
            verifyNoInteractions(jwtUtils);
        }
    }

}

