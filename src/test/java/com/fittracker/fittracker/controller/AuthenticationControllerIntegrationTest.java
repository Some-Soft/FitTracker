package com.fittracker.fittracker.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fittracker.fittracker.entity.User;
import com.fittracker.fittracker.exception.ErrorResponse;
import com.fittracker.fittracker.repository.UserRepository;
import com.fittracker.fittracker.request.LoginRequest;
import com.fittracker.fittracker.request.RegisterRequest;
import com.fittracker.fittracker.response.LoginResponse;
import com.fittracker.fittracker.response.RegisterResponse;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

public class AuthenticationControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    private final static String ENDPOINT = "/auth";

    private final static UUID TEST_UUID = UUID.fromString("948cc727-68e5-455c-ab6d-942e585bde0d");

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
            RegisterRequest registerRequest = new RegisterRequest("user", "user@example.com", "password");

            var responseString = mockMvc.perform(post(URI.create(ENDPOINT + "/register"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

            var expected = new RegisterResponse(TEST_UUID, "user", "user@example.com");
            var result = mapper.readValue(responseString, RegisterResponse.class);

            assertThat(result).usingRecursiveComparison().ignoringFields("id").isEqualTo(expected);
            assertThat(userRepository.findAll()).hasSize(1);
        }

        @Test
        void givenUsernameAlreadyExists_shouldReturnErrorResponse() throws Exception {
            userRepository.save(new User(TEST_UUID, "user", "user@example.com",
                "$2a$10$2gvLjc6wUEgM42M73tQ9ieI2jrAwfxap3X7XsEt//swQvJXyMpVJ6"));
            RegisterRequest registerRequest = new RegisterRequest("user", "anotherUserEmail@example.com", "password");

            var responseString = mockMvc.perform(post(URI.create(ENDPOINT + "/register"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(registerRequest)))
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
            userRepository.save(new User(TEST_UUID, "user", "user@example.com",
                "$2a$10$2gvLjc6wUEgM42M73tQ9ieI2jrAwfxap3X7XsEt//swQvJXyMpVJ6"));
            RegisterRequest registerRequest = new RegisterRequest("anotherUsername", "user@example.com", "password");

            var responseString = mockMvc.perform(post(URI.create(ENDPOINT + "/register"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(registerRequest)))
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
            userRepository.save(new User(TEST_UUID, "user", "user@example.com",
                "$2a$10$2gvLjc6wUEgM42M73tQ9ieI2jrAwfxap3X7XsEt//swQvJXyMpVJ6"));
            LoginRequest loginRequest = new LoginRequest("user", "password");

            var responseString = mockMvc.perform(post(URI.create(ENDPOINT + "/login"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

            var loginResponse = mapper.readValue(responseString, LoginResponse.class);
            assertThat(loginResponse.token()).isNotBlank();
        }

        @Test
        void givenNonexistentUser_shouldReturnErrorResponse() throws Exception {
            userRepository.save(new User(TEST_UUID, "user", "user@example.com",
                "$2a$10$2gvLjc6wUEgM42M73tQ9ieI2jrAwfxap3X7XsEt//swQvJXyMpVJ6"));
            LoginRequest loginRequest = new LoginRequest("badUsername", "password");

            var responseString = mockMvc.perform(post(URI.create(ENDPOINT + "/login"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

            var expected = new ErrorResponse(null, "Bad credentials");
            var result = mapper.readValue(responseString, ErrorResponse.class);

            assertThat(result).isEqualTo(expected);
            assertThat(userRepository.findAll()).hasSize(1);
        }

        @Test
        void givenWrongPassword_shouldReturnErrorResponse() throws Exception {
            userRepository.save(new User(TEST_UUID, "user", "user@example.com",
                "$2a$10$2gvLjc6wUEgM42M73tQ9ieI2jrAwfxap3X7XsEt//swQvJXyMpVJ6"));
            assertThat(userRepository.findAll()).hasSize(1);
            LoginRequest loginRequest = new LoginRequest("user", "badPassword");

            var responseString = mockMvc.perform(post(URI.create(ENDPOINT + "/login"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

            var expected = new ErrorResponse(null, "Bad credentials");
            var result = mapper.readValue(responseString, ErrorResponse.class);

            assertThat(result).isEqualTo(expected);
            assertThat(userRepository.findAll()).hasSize(1);
        }
    }
}
