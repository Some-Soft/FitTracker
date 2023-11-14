package com.fittracker.fittracker.controller;

import com.fittracker.fittracker.entity.User;
import com.fittracker.fittracker.entity.Weight;
import com.fittracker.fittracker.repository.UserRepository;
import com.fittracker.fittracker.repository.WeightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class WeightControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private WeightRepository weightRepository;
    @Autowired
    private UserRepository userRepository;

    private final static String ENDPOINT = "/api/v1/weight";

    private final static String TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyIiwiaWF0IjoxNjk4NDAxMDA0LCJleHAiOjU3MDcwMTQ3NTR9.m8f90mP-vE-sNTNjEP824XXZYRtIhArsXah6HXEjnxYXvekV44D9uvckFCcoIwepkzq9_coD62yPM6UOqyDTLg";

    private UUID testUUID;


    @Override
    protected List<HttpMethod> getProtectedHttpMethods() {
        return List.of(GET, POST);
    }

    @Override
    protected String getEndpoint() {
        return ENDPOINT;
    }


    @BeforeEach
    void beforeEach() {
        createUser();
    }

    @Nested
    class Post {

        @Test
        void givenValidPostRequest_shouldSaveAnReturnWeight() throws Exception {

            mockMvc.perform(post(URI.create(ENDPOINT))
                            .contentType(APPLICATION_JSON)
                            .content("{\"date\":\"2023-08-03\",\"value\":13.2}")
                            .header("Authorization", "Bearer " + TOKEN))
                    .andExpect(status().isCreated())
                    .andExpect(content().string("{\"date\":\"2023-08-03\",\"value\":13.2}"));

            var expected = new Weight(LocalDate.of(2023, 8, 3), 13.2, testUUID);

            var result = weightRepository.findAll();
            assertThat(result).hasSize(1);
            assertThat(result.iterator().next()).
                    usingRecursiveComparison().ignoringFields("id").isEqualTo(expected);

        }

        @Test
        void givenValidPostRequestWithExistingDate_shouldReturnError() throws Exception {
            weightRepository.save(new Weight(LocalDate.of(2023, 8, 3), 13.2, testUUID));
            mockMvc.perform(post(URI.create(ENDPOINT))
                            .contentType(APPLICATION_JSON)
                            .content("{\"date\":\"2023-08-03\",\"value\":13.2}")
                            .header("Authorization", "Bearer " + TOKEN))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("{\"message\":\"Weight already exists for date: 2023-08-03\"}"));

            assertThat(weightRepository.findAll()).hasSize(1);
        }
    }

    @Nested
    class Get {

        @Test
        void givenValidGetRequestWithExistingDate_shouldReturnWeight() throws Exception {
            weightRepository.save(new Weight(LocalDate.of(2023, 8, 3), 13.2, testUUID));
            assertThat(weightRepository.findAll()).hasSize(1);

            mockMvc.perform(get(URI.create(ENDPOINT + "?date=2023-08-03"))
                            .header("Authorization", "Bearer " + TOKEN))
                    .andExpect(status().isOk())
                    .andExpect(content().string("{\"date\":\"2023-08-03\",\"value\":13.2}"));
        }

        @Test
        void givenValidGetRequestWithNonexistentDate_shouldReturnError() throws Exception {

            mockMvc.perform(get(URI.create(ENDPOINT + "?date=2023-08-03"))
                            .header("Authorization", "Bearer " + TOKEN))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("{\"message\":\"Weight not found for date: 2023-08-03\"}"));
        }
    }

    @Nested
    class Put {
        @Test
        void givenValidPutRequest_shouldUpdateAndReturnWeight() throws Exception {
            weightRepository.save(new Weight(LocalDate.of(2023,8,3),13.2, testUUID));

            mockMvc.perform(put(URI.create(ENDPOINT))
                            .contentType(APPLICATION_JSON)
                            .content("{\"date\":\"2023-08-03\",\"value\":14.3}")
                            .header("Authorization", "Bearer " + TOKEN))
                    .andExpect(status().isOk())
                    .andExpect(content().string("{\"date\":\"2023-08-03\",\"value\":14.3}"));

            var expected = new Weight(LocalDate.of(2023, 8, 3), 14.3, testUUID);

            var result = weightRepository.findAll();
            assertThat(result).hasSize(1);
            assertThat(result.iterator().next()).
                    usingRecursiveComparison().ignoringFields("id").isEqualTo(expected);

        }

        @Test
        void givenValidPutRequestWithNonexistentDate_shouldReturnError() throws Exception {
            mockMvc.perform(put(URI.create(ENDPOINT))
                            .contentType(APPLICATION_JSON)
                            .content("{\"date\":\"2023-08-03\",\"value\":13.2}")
                            .header("Authorization", "Bearer " + TOKEN))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("{\"message\":\"Weight not found for date: 2023-08-03\"}"));
        }

    }

    @Nested
    class Delete {
        @Test
        void givenValidDeleteRequest_shouldDeleteAndReturnNoContent() throws Exception {
            weightRepository.save(new Weight(LocalDate.of(2023,8,3),13.2, testUUID));

            mockMvc.perform(delete(URI.create(ENDPOINT + "?date=2023-08-03"))
                            .header("Authorization", "Bearer " + TOKEN))
                    .andExpect(status().isNoContent())
                    .andExpect(content().string(""));


            var result = weightRepository.findAll();
            assertThat(result).hasSize(0);
        }

        @Test
        void givenValidDeleteRequestWithNonexistentDate_shouldReturnError() throws Exception {
            weightRepository.save(new Weight(LocalDate.of(2023,8,3),13.2, testUUID));

            mockMvc.perform(delete(URI.create(ENDPOINT + "?date=2023-08-02"))
                            .header("Authorization", "Bearer " + TOKEN))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("{\"message\":\"Weight not found for date: 2023-08-02\"}"));

            var result = weightRepository.findAll();
            assertThat(result).hasSize(1);
        }
    }

    private void createUser() {
        User user = new User("user","user@example.com");
        user.setPassword("$2a$10$2gvLjc6wUEgM42M73tQ9ieI2jrAwfxap3X7XsEt//swQvJXyMpVJ6");
        User dbUser = userRepository.save(user);
        testUUID = dbUser.getId();
    }
}
