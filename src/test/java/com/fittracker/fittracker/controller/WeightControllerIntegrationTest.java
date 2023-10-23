package com.fittracker.fittracker.controller;

import com.fittracker.fittracker.entity.Weight;
import com.fittracker.fittracker.repository.WeightRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URI;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class WeightControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    WeightRepository weightRepository;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withUsername("testUser")
            .withPassword("testPassword")
            .withDatabaseName("testDatabase");

    @BeforeAll
    static void setUp() {
        System.setProperty("spring.config.name", "test-application");
        postgres.start();
    }

    @BeforeEach
    void beforeSetUp() {
        weightRepository.deleteAll();
    }

    @Test
    void testConnection() {
        assertTrue(postgres.isRunning());
        assertEquals("testUser", postgres.getUsername());
        assertEquals("testPassword", postgres.getPassword());
        assertEquals("testDatabase", postgres.getDatabaseName());
    }

    @Test
    void givenValidPostRequest_shouldSaveAnReturnWeight() throws Exception {
        assertThat(weightRepository.findAll()).hasSize(0);

        mockMvc.perform(post(URI.create("/api/v1/weight"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"date\":\"2023-08-03\",\"value\":13.2}"))
                .andExpect(status().isCreated())
                .andExpect(content().string("{\"date\":\"2023-08-03\",\"value\":13.2}"));

        var expected = new Weight(LocalDate.of(2023, 8, 3), 13.2);

        var result = weightRepository.findAll();
        assertThat(result).hasSize(1);
        assertThat(result.iterator().next()).
                usingRecursiveComparison().ignoringFields("id").isEqualTo(expected);

    }
    @Test
    void givenValidPostRequestWithExistingDate_shouldReturnError() throws Exception {
        assertThat(weightRepository.findAll()).hasSize(0);
        weightRepository.save(new Weight(LocalDate.of(2023, 8, 3), 13.2));
        mockMvc.perform(post(URI.create("/api/v1/weight"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"date\":\"2023-08-03\",\"value\":13.2}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{\"message\":\"Weight already exists for date: 2023-08-03\"}"));

        assertThat(weightRepository.findAll()).hasSize(1);
    }
}
