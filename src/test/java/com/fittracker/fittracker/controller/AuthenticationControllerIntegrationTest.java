package com.fittracker.fittracker.controller;

import static com.fittracker.fittracker.dataprovider.Entity.user;
import static com.fittracker.fittracker.dataprovider.Request.loginRequest;
import static com.fittracker.fittracker.dataprovider.Request.loginRequestWithPassword;
import static com.fittracker.fittracker.dataprovider.Request.loginRequestWithUsername;
import static com.fittracker.fittracker.dataprovider.Request.registerRequest;
import static com.fittracker.fittracker.dataprovider.Request.registerRequestWithEmail;
import static com.fittracker.fittracker.dataprovider.Request.registerRequestWithUsername;
import static com.fittracker.fittracker.dataprovider.Response.registerResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fittracker.fittracker.exception.ErrorResponse;
import com.fittracker.fittracker.repository.UserRepository;
import com.fittracker.fittracker.response.LoginResponse;
import com.fittracker.fittracker.response.RegisterResponse;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

public class AuthenticationControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    private final static String ENDPOINT = "/auth";

    private final ObjectMapper mapper = new ObjectMapper();

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
            var responseString = mockMvc.perform(post(URI.create(ENDPOINT + "/register"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(registerRequest())))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

            var expected = registerResponse();
            var result = mapper.readValue(responseString, RegisterResponse.class);

            assertThat(result).usingRecursiveComparison().ignoringFields("id").isEqualTo(expected);
            assertThat(userRepository.findAll()).hasSize(1);
        }

        @Test
        void givenUsernameAlreadyExists_shouldReturnErrorResponse() throws Exception {
            userRepository.save(user());

            var responseString = mockMvc.perform(post(URI.create(ENDPOINT + "/register"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(registerRequestWithEmail("anotherUserEmail@example.com"))))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

            var expected = new ErrorResponse(null,
                "User already exists for username/email provided: user/anotherUserEmail@example.com");
            var result = mapper.readValue(responseString, ErrorResponse.class);

            assertThat(result).isEqualTo(expected);
            assertThat(userRepository.findAll()).hasSize(1);
        }

        @Test
        void givenEmailAlreadyExists_shouldReturnErrorResponse() throws Exception {
            userRepository.save(user());

            var responseString = mockMvc.perform(post(URI.create(ENDPOINT + "/register"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(registerRequestWithUsername("anotherUsername"))))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

            var expected = new ErrorResponse(null,
                "User already exists for username/email provided: anotherUsername/user@example.com");
            var result = mapper.readValue(responseString, ErrorResponse.class);

            assertThat(result).isEqualTo(expected);
            assertThat(userRepository.findAll()).hasSize(1);
        }

    }

    @Nested
    class Login {

        @Test
        void givenValidCredentials_shouldReturnLoginResponse() throws Exception {
            userRepository.save(user());

            var responseString = mockMvc.perform(post(URI.create(ENDPOINT + "/login"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(loginRequest())))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

            var loginResponse = mapper.readValue(responseString, LoginResponse.class);
            assertThat(loginResponse.token()).isNotBlank();
        }

        @Test
        void givenNonexistentUser_shouldReturnErrorResponse() throws Exception {
            userRepository.save(user());

            var responseString = mockMvc.perform(post(URI.create(ENDPOINT + "/login"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(loginRequestWithUsername("badUsername"))))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

            var expected = new ErrorResponse(null, "Bad credentials");
            var result = mapper.readValue(responseString, ErrorResponse.class);

            assertThat(result).isEqualTo(expected);
            assertThat(userRepository.findAll()).hasSize(1);
        }

        @Test
        void givenWrongPassword_shouldReturnErrorResponse() throws Exception {
            userRepository.save(user());
            assertThat(userRepository.findAll()).hasSize(1);

            var responseString = mockMvc.perform(post(URI.create(ENDPOINT + "/login"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(loginRequestWithPassword("badPassword"))))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

            var expected = new ErrorResponse(null, "Bad credentials");
            var result = mapper.readValue(responseString, ErrorResponse.class);

            assertThat(result).isEqualTo(expected);
            assertThat(userRepository.findAll()).hasSize(1);
        }
    }
}
