package com.fittracker.fittracker.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static java.net.URI.create;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("test")
abstract class BaseIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @BeforeAll
    static void setUp() {
        postgres.start();
    }

    protected abstract List<HttpMethod> getProtectedHttpMethods();
    protected abstract String getEndpoint();

    @Test
    void givenRequestWithNoAuthorization_shouldReturnUnauthorizedError() throws Exception {
        for (HttpMethod httpMethod : getProtectedHttpMethods()) {
            mockMvc.perform(request(httpMethod, create(getEndpoint())))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().string(""));
        }
    }

}
