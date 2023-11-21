package com.fittracker.fittracker.controller;

import static com.fittracker.fittracker.dataprovider.Request.loginRequest;
import static com.fittracker.fittracker.dataprovider.Request.loginRequestWithPassword;
import static com.fittracker.fittracker.dataprovider.Request.loginRequestWithUsername;
import static com.fittracker.fittracker.dataprovider.Request.registerRequest;
import static com.fittracker.fittracker.dataprovider.Request.registerRequestWithEmail;
import static com.fittracker.fittracker.dataprovider.Request.registerRequestWithPassword;
import static com.fittracker.fittracker.dataprovider.Request.registerRequestWithUsername;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.of;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fittracker.fittracker.exception.ErrorResponse;
import com.fittracker.fittracker.exception.ErrorResponseMapper;
import com.fittracker.fittracker.request.LoginRequest;
import com.fittracker.fittracker.request.RegisterRequest;
import com.fittracker.fittracker.security.JwtAuthenticationFilter;
import com.fittracker.fittracker.service.AuthenticationService;
import java.net.URI;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = AuthenticationController.class, excludeFilters = {
    @ComponentScan.Filter(type = ASSIGNABLE_TYPE, value = JwtAuthenticationFilter.class)})
@Import({ErrorResponseMapper.class, ObjectMapper.class})
@AutoConfigureMockMvc(addFilters = false)
class AuthenticationControllerValidationTest {

    private static final String ENDPOINT = "/auth";

    @MockBean
    private AuthenticationService authenticationService;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @Nested
    class Register {

        @Test
        void givenValidRegisterRequest_shouldReturnCreated() throws Exception {
            RegisterRequest registerRequest = registerRequest();
            mockMvc
                .perform(post(URI.create(ENDPOINT + "/register"))
                    .contentType(APPLICATION_JSON)
                    .content(mapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().string(""));

            verify(authenticationService).register(registerRequest);
        }

        @ParameterizedTest
        @MethodSource("nullFieldTestDataProvider")
        void givenRegisterRequestWithNullField_shouldReturnErrorResponse(RegisterRequest registerRequest, String field,
            String expectedMessage) throws Exception {
            var requestBodyString = mapper.writeValueAsString(registerRequest);
            var responseString = mockMvc
                .perform(post(URI.create(ENDPOINT + "/register"))
                    .contentType(APPLICATION_JSON)
                    .content(requestBodyString))
                .andExpect(status().is(BAD_REQUEST.value()))
                .andReturn().getResponse().getContentAsString();

            var expected = new ErrorResponse(field, expectedMessage);
            var response = mapper.readValue(responseString, ErrorResponse.class);

            assertThat(response).isEqualTo(expected);
            verifyNoInteractions(authenticationService);
        }

        private static Stream<Arguments> nullFieldTestDataProvider() {
            return Stream.of(
                of(registerRequestWithUsername(null), "username", "Username must not be null"),
                of(registerRequestWithEmail(null), "email", "Email must not be null"),
                of(registerRequestWithPassword(null), "password", "Password must not be null")
            );
        }

        @ParameterizedTest
        @MethodSource("incorrectLengthTestDataProvider")
        void givenRegisterRequestWithIncorrectLengthField_shouldReturnErrorResponse(RegisterRequest registerRequest,
            String field, String expectedMessage) throws Exception {
            var responseString = mockMvc
                .perform(post(URI.create(ENDPOINT + "/register"))
                    .contentType(APPLICATION_JSON)
                    .content(mapper.writeValueAsString(registerRequest)))
                .andExpect(status().is(BAD_REQUEST.value()))
                .andReturn().getResponse().getContentAsString();

            var expected = new ErrorResponse(field, expectedMessage);
            var response = mapper.readValue(responseString, ErrorResponse.class);

            assertThat(response).isEqualTo(expected);
            verifyNoInteractions(authenticationService);
        }

        private static Stream<Arguments> incorrectLengthTestDataProvider() {
            return Stream.of(
                of(registerRequestWithUsername("a"), "username",
                    "Username must be between 3 and 64 characters"),
                of(registerRequestWithUsername("a".repeat(65)), "username",
                    "Username must be between 3 and 64 characters"),
                of(registerRequestWithEmail("a"), "email",
                    "Email must be between 3 and 254 characters"),
                of(registerRequestWithEmail("a".repeat(255)), "email",
                    "Email must be between 3 and 254 characters"),
                of(registerRequestWithPassword("a"), "password",
                    "Password must be between 3 and 30 characters"),
                of(registerRequestWithPassword("a".repeat(31)), "password",
                    "Password must be between 3 and 30 characters")
            );
        }

    }

    @Nested
    class Login {

        @Test
        void givenValidLoginRequest_shouldReturnOk() throws Exception {
            LoginRequest loginRequest = loginRequest();

            mockMvc
                .perform(post(URI.create(ENDPOINT + "/login"))
                    .contentType(APPLICATION_JSON)
                    .content(mapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

            verify(authenticationService).login(loginRequest);
            verifyNoMoreInteractions(authenticationService);
        }

        @ParameterizedTest
        @MethodSource("nullFieldsTestDataProvider")
        void givenLoginRequestWithNullField_shouldReturnErrorResponse(LoginRequest loginRequest, String field,
            String expectedMessage) throws Exception {
            var responseString = mockMvc
                .perform(post(URI.create(ENDPOINT + "/login"))
                    .contentType(APPLICATION_JSON)
                    .content(mapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

            var expected = new ErrorResponse(field, expectedMessage);
            var response = mapper.readValue(responseString, ErrorResponse.class);

            assertThat(response).isEqualTo(expected);
            verifyNoInteractions(authenticationService);
        }

        private static Stream<Arguments> nullFieldsTestDataProvider() {
            return Stream.of(
                of(loginRequestWithUsername(null), "username", "Username must not be null"),
                of(loginRequestWithPassword(null), "password", "Password must not be null")
            );
        }

    }

}