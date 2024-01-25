package com.somesoft.fittracker.service;

import static com.somesoft.fittracker.dataprovider.Entity.userWithPassword;
import static com.somesoft.fittracker.dataprovider.Request.loginRequest;
import static com.somesoft.fittracker.dataprovider.Request.registerRequest;
import static com.somesoft.fittracker.dataprovider.Response.loginResponse;
import static com.somesoft.fittracker.dataprovider.Response.registerResponse;
import static com.somesoft.fittracker.dataprovider.TestHelper.assertEqualRecursiveIgnoring;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.somesoft.fittracker.entity.User;
import com.somesoft.fittracker.exception.UserAlreadyExistsException;
import com.somesoft.fittracker.repository.UserRepository;
import com.somesoft.fittracker.security.JwtUtils;
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

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

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

    private static final String TEST_PASSWORD = "password";

    @BeforeEach
    void beforeEach() {
        lenient().when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn("encodedPassword");
        lenient().when(userRepository.save(any()))
            .thenReturn(userWithPassword(TEST_PASSWORD));
    }

    @Nested
    class Register {

        @Test
        void givenNonexistentUser_shouldReturnRegisterResponse() {
            when(userRepository.existsByUsernameOrEmail(any(), any())).thenReturn(false);

            var expected = registerResponse();
            var result = authenticationService.register(registerRequest());

            assertThat(result).isEqualTo(expected);
            verify(userRepository).existsByUsernameOrEmail("user", "user@example.com");
            verify(passwordEncoder).encode(TEST_PASSWORD);
            verify(userRepository).save(userCaptor.capture());
            assertEqualRecursiveIgnoring(userCaptor.getValue(), userWithPassword("encodedPassword"), "id");
            verifyNoMoreInteractions(userRepository);
        }

        @Test
        void givenExistingUser_shouldThrowException() {
            when(userRepository.existsByUsernameOrEmail(any(), any())).thenReturn(true);

            assertThatThrownBy(() -> authenticationService.register(registerRequest()))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("User already exists for username/email provided: user/user@example.com");

            verify(userRepository).existsByUsernameOrEmail(any(), any());
            verifyNoMoreInteractions(userRepository);
            verifyNoInteractions(passwordEncoder);
        }

    }

    @Nested
    class Login {

        private static final UsernamePasswordAuthenticationToken USERNAME_PASSWORD_AUTHENTICATION_TOKEN = new UsernamePasswordAuthenticationToken(
            "user", TEST_PASSWORD);

        @Test
        void givenValidCredentials_shouldReturnLoginResponse() {
            when(authenticationManager.authenticate(USERNAME_PASSWORD_AUTHENTICATION_TOKEN)).thenReturn(authentication);
            when(jwtUtils.generateToken(any())).thenReturn("token");

            var result = authenticationService.login(loginRequest());

            assertThat(result).isEqualTo(loginResponse());
            verify(jwtUtils).generateToken(authentication);
            verify(authenticationManager).authenticate(USERNAME_PASSWORD_AUTHENTICATION_TOKEN);
            verifyNoMoreInteractions(jwtUtils);
            verifyNoMoreInteractions(authenticationManager);
        }

        @Test
        void givenInvalidCredentials_shouldThrowException() {
            when(authenticationManager.authenticate(USERNAME_PASSWORD_AUTHENTICATION_TOKEN)).thenThrow(
                BadCredentialsException.class);

            assertThatThrownBy(() -> authenticationService.login(loginRequest()))
                .isInstanceOf(BadCredentialsException.class);

            verify(authenticationManager).authenticate(USERNAME_PASSWORD_AUTHENTICATION_TOKEN);
            verifyNoMoreInteractions(authenticationManager);
            verifyNoInteractions(jwtUtils);
        }
    }

}

