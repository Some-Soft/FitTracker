package com.somesoft.fittracker.controller;


import static com.somesoft.fittracker.dataprovider.Request.loginRequestWithPassword;
import static com.somesoft.fittracker.dataprovider.Request.loginRequestWithUsername;
import static com.somesoft.fittracker.dataprovider.Request.registerRequest;
import static com.somesoft.fittracker.dataprovider.Request.registerRequestWithEmail;
import static com.somesoft.fittracker.dataprovider.Request.registerRequestWithUsername;
import static com.somesoft.fittracker.dataprovider.Response.registerResponse;
import static com.somesoft.fittracker.dataprovider.TestHelper.assertEqualRecursiveIgnoring;
import static java.net.URI.create;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.somesoft.fittracker.exception.ErrorResponse;
import com.somesoft.fittracker.response.LoginResponse;
import com.somesoft.fittracker.response.RegisterResponse;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

public class AuthenticationControllerIntegrationTest extends BaseIntegrationTest {

    private static final String ENDPOINT = "/auth";

    @Override
    protected List<HttpMethod> getProtectedHttpMethods() {
        return List.of();
    }

    @Override
    protected String getEndpoint() {
        return ENDPOINT;
    }

    @Nested
    class Register {

        @Test
        void givenUsernameNorEmailExists_shouldReturnRegisterResponse() throws Exception {
            var expectedResponse = registerResponse();

            var response = makeUnauthorizedPostRequest(ENDPOINT + "/register", registerRequest(), CREATED,
                RegisterResponse.class);

            assertEqualRecursiveIgnoring(response, expectedResponse, "id");
        }

        @Test
        void givenUsernameAlreadyExists_shouldReturnErrorResponse() throws Exception {
            var expectedResponse = ErrorResponse.withMessage(
                "User already exists for username/email provided: user/anotherUserEmail@example.com");

            var response = makeUnauthorizedPostRequest(ENDPOINT + "/register",
                registerRequestWithEmail("anotherUserEmail@example.com"), BAD_REQUEST, ErrorResponse.class);

            assertThat(response).isEqualTo(expectedResponse);
        }

        @Test
        void givenEmailAlreadyExists_shouldReturnErrorResponse() throws Exception {
            var expectedResponse = ErrorResponse.withMessage(
                "User already exists for username/email provided: anotherUsername/user@example.com");

            var response = makeUnauthorizedPostRequest(ENDPOINT + "/register",
                registerRequestWithUsername("anotherUsername"), BAD_REQUEST, ErrorResponse.class);

            assertThat(response).isEqualTo(expectedResponse);
        }
    }

    @Nested
    class Login {

        @Test
        void givenValidCredentials_shouldReturnLoginResponse() throws Exception {
            var response = makeUnauthorizedPostRequest(ENDPOINT + "/login", loginRequestWithUsername("testuser"), OK,
                LoginResponse.class);

            assertThat(response.token()).isNotBlank();
        }

        @Test
        void givenNonexistentUser_shouldReturnErrorResponse() throws Exception {
            var expectedResponse = ErrorResponse.withMessage("Bad credentials");

            var response = makeUnauthorizedPostRequest(ENDPOINT + "/login", loginRequestWithUsername("badUsername"),
                UNAUTHORIZED, ErrorResponse.class);

            assertThat(response).isEqualTo(expectedResponse);
        }

        @Test
        void givenWrongPassword_shouldReturnErrorResponse() throws Exception {
            var expectedResponse = ErrorResponse.withMessage("Bad credentials");

            var response = makeUnauthorizedPostRequest(ENDPOINT + "/login", loginRequestWithPassword("badPassword"),
                UNAUTHORIZED, ErrorResponse.class);

            assertThat(response).isEqualTo(expectedResponse);
        }
    }

    private <T> T makeUnauthorizedPostRequest(String endpoint, Object requestBody, HttpStatus expectedStatus,
        Class<T> responseClass) throws Exception {
        var responseString = mockMvc.perform(post(create(endpoint))
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestBody)))
            .andExpect(status().is(expectedStatus.value()))
            .andReturn().getResponse().getContentAsString();

        return mapper.readValue(responseString, responseClass);
    }
}
