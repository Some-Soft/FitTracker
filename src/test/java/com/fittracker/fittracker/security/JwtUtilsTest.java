package com.fittracker.fittracker.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;

import com.fittracker.fittracker.config.JwtConfig;
import com.fittracker.fittracker.entity.UserDetails;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {

    private static final String TEST_SECRET = "3Wlamn5wuIm2698NNKnplSq2GY8vqbzBCRynuvh1swOKzp5Ivr1zaQ9seoEyk5mFNEyvkxvU9pExIqdm";
    private static final UUID testUUID = UUID.fromString("5ed73615-eea8-493e-b406-393f3b0af4aa");
    private static final int JWT_SIGNATURE_LENGTH = 86;
    private static final int TEST_TOKEN_EXPIRATION_PERIOD_MINUTES = 60;
    @Mock
    private Authentication authentication;
    @Mock
    private UserDetails userDetails;
    private JwtUtils jwtUtils;
    private JwtConfig jwtConfig;


    @BeforeEach
    public void beforeEach() {
        lenient().when(authentication.getName()).thenReturn("user");
        lenient().when(authentication.getPrincipal()).thenReturn(userDetails);
        lenient().when(userDetails.id()).thenReturn(testUUID);
        jwtConfig = new JwtConfig(TEST_SECRET, TEST_TOKEN_EXPIRATION_PERIOD_MINUTES);
        jwtUtils = new JwtUtils(jwtConfig);
    }


    @Test
    void generateToken_givenNameInAuthentication_shouldReturnTokenInValidFormat() {
        String token = jwtUtils.generateToken(authentication);

        String[] parts = token.split("\\.");

        assertThat(parts).hasSize(3);
        assertThat(parts[2]).hasSize(JWT_SIGNATURE_LENGTH);
    }

    @Test
    void getUsernameFromToken_givenToken_shouldReturnUsername() {
        var token = jwtUtils.generateToken(authentication);
        var result = jwtUtils.getUsernameFromToken(token);

        assertThat(result).isEqualTo("user");
    }

    @Test
    void getUserIdFromToken_givenToken_shouldReturnUserId() {
        var token = jwtUtils.generateToken(authentication);
        var result = jwtUtils.getUserIdFromToken(token);

        assertThat(result).isEqualTo(testUUID);
    }

    @Nested
    class IsTokenValid {

        @Test
        void givenValidToken_shouldReturnTrue() {
            String token = jwtUtils.generateToken(authentication);

            var result = jwtUtils.isTokenValid(token);

            assertThat(result).isTrue();
        }

        @Test
        void givenTokenWithManipulatedSignature_shouldReturnFalse() {
            String token = jwtUtils.generateToken(authentication) + "a";

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