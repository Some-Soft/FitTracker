package com.fittracker.fittracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fittracker.fittracker.exception.ErrorResponse;
import com.fittracker.fittracker.exception.ErrorResponseMapper;
import com.fittracker.fittracker.request.LoginRequest;
import com.fittracker.fittracker.request.RegisterRequest;
import com.fittracker.fittracker.security.JwtAuthenticationFilter;
import com.fittracker.fittracker.service.AuthenticationService;
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

import java.net.URI;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.of;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        void givenValidRegisterRequest_shouldReturnRegisterResponse() throws Exception {

            RegisterRequest registerRequest = new RegisterRequest("user", "user@example.com", "password");
            mockMvc
                    .perform(post(URI.create(ENDPOINT + "/register"))
                            .contentType("application/json")
                            .content(mapper.writeValueAsString(registerRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(content().string(""));

            verify(authenticationService).register(registerRequest);
        }

        @ParameterizedTest
        @MethodSource("nullFieldTestDataProvider")
        void givenRegisterRequestWithNullField_shouldReturnErrorResponse(RegisterRequest registerRequest, String field, String expectedMessage) throws Exception {

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
                    of(new RegisterRequest(null, "user@example.com", "password"), "username", "Username must not be null"),
                    of(new RegisterRequest("user", null, "password"), "email", "Email must not be null"),
                    of(new RegisterRequest("user", "user@example.com", null), "password", "Password must not be null")
            );
        }

        @ParameterizedTest
        @MethodSource("incorrectLengthTestDataProvider")
        void givenRegisterRequestWithIncorrectLengthField_shouldReturnErrorResponse(RegisterRequest registerRequest, String field, String expectedMessage) throws Exception {

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
            String s = "a";
            return Stream.of(
                    of(new RegisterRequest("a", "user@example.com", "password"), "username", "Username must be between 3 and 64 characters"),
                    of(new RegisterRequest(s.repeat(65), "user@example.com", "password"), "username", "Username must be between 3 and 64 characters"),
                    of(new RegisterRequest("user", "a", "password"), "email", "Email must be between 3 and 254 characters"),
                    of(new RegisterRequest("user", s.repeat(255), "password"), "email", "Email must be between 3 and 254 characters"),
                    of(new RegisterRequest("user", "user@example.com", "a"), "password", "Password must be between 3 and 30 characters"),
                    of(new RegisterRequest("user", "user@example.com", s.repeat(31)), "password", "Password must be between 3 and 30 characters")
            );
        }

    }


    @Nested
    class Login {

        @Test
        void givenValidLoginRequest_shouldReturnLoginResponse() throws Exception {
            LoginRequest loginRequest = new LoginRequest("user", "password");

            mockMvc
                    .perform(post(URI.create(ENDPOINT + "/login"))
                            .contentType("application/json")
                            .content(mapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andExpect(content().string(""));

            verify(authenticationService).login(loginRequest);
            verifyNoMoreInteractions(authenticationService);
        }

        @ParameterizedTest
        @MethodSource("nullFieldsTestDataProvider")
        void givenLoginRequestWithNullField_shouldReturnErrorResponse(LoginRequest loginRequest, String field, String expectedMessage) throws Exception {
            var responseString = mockMvc
                    .perform(post(URI.create(ENDPOINT + "/login"))
                            .contentType("application/json")
                            .content(mapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            var expected = new ErrorResponse(field,expectedMessage);
            var response = mapper.readValue(responseString, ErrorResponse.class);

            assertThat(response).isEqualTo(expected);
            verifyNoInteractions(authenticationService);
        }

        private static Stream<Arguments> nullFieldsTestDataProvider() {
            return Stream.of(
                    of(new LoginRequest(null, "password"), "username", "Username must not be null"),
                    of(new LoginRequest("user", null), "password", "Password must not be null")
            );
        }


    }


}