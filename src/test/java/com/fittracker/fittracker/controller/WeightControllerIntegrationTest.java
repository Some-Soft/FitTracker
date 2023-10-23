package com.fittracker.fittracker.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
public class WeightControllerIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withUsername("testUser")
            .withPassword("testPassword")
            .withDatabaseName("testDatabase");

    @BeforeAll
    static void setup(){
        System.setProperty("spring.config.name", "test-application");
        postgres.start();
    }

    @Test
    void testConnection() {
        assertTrue(postgres.isRunning());
        assertEquals("testUser", postgres.getUsername());
        assertEquals("testPassword", postgres.getPassword());
        assertEquals("testDatabase", postgres.getDatabaseName());
    }
}
