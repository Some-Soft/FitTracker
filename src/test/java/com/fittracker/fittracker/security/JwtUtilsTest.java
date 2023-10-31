package com.fittracker.fittracker.security;

import com.fittracker.fittracker.config.JwtConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {

    private static final String TEST_SECRET = "3Wlamn5wuIm2698NNKnplSq2GY8vqbzBCRynuvh1swOKzp5Ivr1zaQ9seoEyk5mFNEyvkxvU9pExIqdm";
    private static final int JWT_SIGNATURE_LENGTH = 86;
    private static final int TEST_TOKEN_EXPIRATION_PERIOD_MINUTES = 60;
    @Mock
    private Authentication authentication;
    private JwtUtils jwtUtils;

    private JwtConfig jwtConfig;

    @BeforeEach
    public void setUp() {
        when(authentication.getName()).thenReturn("user");
        jwtConfig = new JwtConfig(TEST_SECRET, TEST_TOKEN_EXPIRATION_PERIOD_MINUTES);
        jwtUtils = new JwtUtils(jwtConfig);
    }


    @Test
    void givenNameInAuthentication_ShouldReturnTokenInValidFormat() {
        String token = jwtUtils.generateToken(authentication);

        String[] parts = token.split("\\.");

        assertThat(parts).hasSize(3);
        assertThat(parts[2]).hasSize(JWT_SIGNATURE_LENGTH);
    }

    @Test
    void getUsernameFromToken() {

        var token = jwtUtils.generateToken(authentication);
        var result = jwtUtils.getUsernameFromToken(token);

        assertThat(result).isEqualTo("user");
    }
    @Nested
    class TokenValid {

        @Test
        void givenValidToken_shouldReturnTrue() {
            String token = jwtUtils.generateToken(authentication);

            var result = jwtUtils.isTokenValid(token);

            assertThat(result).isTrue();
        }

        @Test
        void givenTokenWithManipulatedSignature_shouldReturnFalse() {
            String token = jwtUtils.generateToken(authentication);
            token += "a";

            var result = jwtUtils.isTokenValid(token);

            assertThat(result).isFalse();
        }

        @Test
        void givenTokenOfInvalidFormat_shouldReturnFalse() {
            String token = "thisIsNotATokenFormat";

            var result = jwtUtils.isTokenValid(token);

            assertThat(result).isFalse();
        }

        @Test
        void givenExpiredToken_shouldReturnFalse() {
            jwtConfig = new JwtConfig(TEST_SECRET, -1);
            jwtUtils = new JwtUtils(jwtConfig);
            String token = jwtUtils.generateToken(authentication);

            var result = jwtUtils.isTokenValid(token);

            assertThat(result).isFalse();
        }

        @Test
        void givenTokenSignedWithAnotherSignature_shouldReturnFalse() {
            String token = jwtUtils.generateToken(authentication);

            jwtConfig = new JwtConfig(TEST_SECRET + "a", TEST_TOKEN_EXPIRATION_PERIOD_MINUTES);
            jwtUtils = new JwtUtils(jwtConfig);

            var result = jwtUtils.isTokenValid(token);

            assertThat(result).isFalse();
        }
    }
}