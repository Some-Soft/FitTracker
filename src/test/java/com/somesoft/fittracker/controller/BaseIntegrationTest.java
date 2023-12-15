package com.somesoft.fittracker.controller;


import static com.github.dockerjava.api.model.Ports.Binding.bindPort;
import static java.net.URI.create;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("test")
abstract class BaseIntegrationTest {

    private static final String USER = "user";
    private static final String PASSWORD = "password";
    private static final int EXPOSED_PORT = 5432;
    private static final int EXTERNAL_MAPPED_PORT = 54320;
    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    private static final String AUTHORIZATION_HEADER_VALUE_PREFIX = "Bearer ";
    private static final String TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJ1c2VySWQiOiI5NDhjYzcyNy02OGU1LTQ1NWMtYWI2ZC05NDJlNTg1YmRlMGQiLCJzdWIiOiJ1c2VyIiwiaWF0IjoxNzAwMTMwMDQ4LCJleHAiOjIwMTU0OTAwNDh9.-SAg73-4JrzzoSfvtvbyxOZzYCRxnx4wIRiNHlvdTeK5BbOmlL-FG-bNbg1eJ76qUG994OfHQmmQwmfv0JZIWg";

    @ServiceConnection
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    protected static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    protected MockMvc mockMvc;

    @BeforeAll
    protected static void startDatabaseContainer() {
        mapper.findAndRegisterModules();
        postgres
            .withUsername(USER)
            .withPassword(PASSWORD)
            .withExposedPorts(EXPOSED_PORT)
            .withCreateContainerCmdModifier(BaseIntegrationTest::mapToExternalPort)
            .start();
    }

    private static void mapToExternalPort(CreateContainerCmd cmd) {
        cmd.withHostConfig(new HostConfig().withPortBindings(new PortBinding(
            bindPort(EXTERNAL_MAPPED_PORT),
            new ExposedPort(EXPOSED_PORT))));
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

    protected String makeRequest(String endpoint, HttpMethod httpMethod, HttpStatus expectedStatus) throws Exception {
        return performRequest(endpoint, httpMethod, expectedStatus);
    }

    protected <T> T makeRequest(String endpoint, HttpMethod httpMethod, HttpStatus expectedStatus,
        Class<T> responseClass) throws Exception {
        var responseString = performRequest(endpoint, httpMethod, expectedStatus);

        return mapper.readValue(responseString, responseClass);
    }

    protected <T> T makeRequestWithBody(HttpMethod httpMethod, Object requestBody, HttpStatus expectedStatus,
        Class<T> responseClass) throws Exception {
        return makeRequestWithBody(getEndpoint(), httpMethod, requestBody, expectedStatus, responseClass);
    }

    protected <T> T makeRequestWithBody(String endpoint, HttpMethod httpMethod, Object requestBody,
        HttpStatus expectedStatus,
        Class<T> responseClass) throws Exception {
        var responseString = mockMvc.perform(request(httpMethod, create(endpoint))
                .header(AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE_PREFIX + TOKEN)
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestBody)))
            .andExpect(status().is(expectedStatus.value()))
            .andReturn().getResponse().getContentAsString();

        return mapper.readValue(responseString, responseClass);
    }


    private String performRequest(String endpoint, HttpMethod httpMethod, HttpStatus expectedStatus) throws Exception {
        return mockMvc.perform(request(httpMethod, create(endpoint))
                .header(AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE_PREFIX + TOKEN))
            .andExpect(status().is(expectedStatus.value()))
            .andReturn().getResponse().getContentAsString();
    }
}
