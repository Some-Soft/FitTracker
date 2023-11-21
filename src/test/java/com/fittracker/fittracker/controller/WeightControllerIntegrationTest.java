package com.fittracker.fittracker.controller;

import static com.fittracker.fittracker.dataprovider.Entity.user;
import static com.fittracker.fittracker.dataprovider.Entity.weightWithUuid;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fittracker.fittracker.entity.User;
import com.fittracker.fittracker.entity.Weight;
import com.fittracker.fittracker.repository.UserRepository;
import com.fittracker.fittracker.repository.WeightRepository;
import com.fittracker.fittracker.request.LoginRequest;
import com.fittracker.fittracker.service.AuthenticationService;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;

public class WeightControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private WeightRepository weightRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationService authenticationService;

    private final static String ENDPOINT = "/api/v1/weight";

    private String token;

    private UUID testUuid;


    @Override
    protected List<HttpMethod> getProtectedHttpMethods() {
        return List.of(GET, POST, PUT, DELETE);
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
                    .content("{\"date\":\"2023-10-10\",\"value\":100.1}")
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(content().string("{\"date\":\"2023-10-10\",\"value\":100.1}"));

            var expected = weightWithUuid(testUuid);

            var result = weightRepository.findAll();
            assertThat(result).hasSize(1);
            assertThat(result.iterator().next()).
                usingRecursiveComparison().ignoringFields("id").isEqualTo(expected);

        }

        @Test
        void givenValidPostRequestWithExistingDate_shouldReturnError() throws Exception {
            weightRepository.save(weightWithUuid(testUuid));
            mockMvc.perform(post(URI.create(ENDPOINT))
                    .contentType(APPLICATION_JSON)
                    .content("{\"date\":\"2023-10-10\",\"value\":100.1}")
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{\"message\":\"Weight already exists for date: 2023-10-10\"}"));

            assertThat(weightRepository.findAll()).hasSize(1);
        }
    }

    @Nested
    class Get {

        @Nested
        class WeightEndpoint {

            @Test
            void givenValidGetRequestWithExistingDate_shouldReturnWeight() throws Exception {
                weightRepository.save(weightWithUuid(testUuid));

                mockMvc.perform(get(URI.create(ENDPOINT + "?date=2023-10-10"))
                        .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(content().string("{\"date\":\"2023-10-10\",\"value\":100.1}"));
            }

            @Test
            void givenValidGetRequestWithNonexistentDate_shouldReturnError() throws Exception {

                mockMvc.perform(get(URI.create(ENDPOINT + "?date=2023-10-10"))
                        .header("Authorization", "Bearer " + token))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("{\"message\":\"Weight not found for date: 2023-10-10\"}"));
            }

        }

        @Nested
        class WeightsEndpoint {

            @BeforeEach
            public void beforeEach() {
                List<Weight> weights = List.of(
                    new Weight(LocalDate.of(2023, 1, 1), 100.0, testUuid),
                    new Weight(LocalDate.of(2023, 1, 10), 95.5, testUuid),
                    new Weight(LocalDate.of(2023, 2, 20), 98.2, testUuid)
                );
                weightRepository.saveAll(weights);
            }

            @Test
            void givenDateRange_shouldReturnListOfWeightResponses() throws Exception {
                mockMvc.perform(get(URI.create(ENDPOINT + "s?startDate=2023-01-02&endDate=2023-03-04"))
                        .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(content().string("[{\"date\":\"2023-01-10\",\"value\":95.5}," +
                        "{\"date\":\"2023-02-20\",\"value\":98.2}]"));
            }

            @Test
            void givenDateRangeWithNoWeights_shouldReturnEmptyList() throws Exception {
                mockMvc.perform(get(URI.create(ENDPOINT + "s?startDate=2023-02-21&endDate=2023-03-04"))
                        .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(content().string("[]"));
            }

            @Test
            void givenStartDateEqualToEndDateWithWeight_shouldReturnListWithOneElement() throws Exception {
                mockMvc.perform(get(URI.create(ENDPOINT + "s?startDate=2023-01-10&endDate=2023-01-10"))
                        .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(content().string("[{\"date\":\"2023-01-10\",\"value\":95.5}]"));
            }

            @Test
            void givenStartDateAfterEndDate_shouldReturnError() throws Exception {
                mockMvc.perform(get(URI.create(ENDPOINT + "s?startDate=2023-01-10&endDate=2023-01-09"))
                        .header("Authorization", "Bearer " + token))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("{\"message\":\"Start date cannot be after end date\"}"));
            }

        }

    }

    @Nested
    class Put {

        @Test
        void givenValidPutRequest_shouldUpdateAndReturnWeight() throws Exception {
            weightRepository.save(weightWithUuid(testUuid));

            mockMvc.perform(put(URI.create(ENDPOINT))
                    .contentType(APPLICATION_JSON)
                    .content("{\"date\":\"2023-10-10\",\"value\":14.3}")
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"date\":\"2023-10-10\",\"value\":14.3}"));

            var expected = new Weight(LocalDate.of(2023, 10, 10), 14.3, testUuid);

            var result = weightRepository.findAll();
            assertThat(result).hasSize(1);
            assertThat(result.iterator().next()).
                usingRecursiveComparison().ignoringFields("id").isEqualTo(expected);

        }

        @Test
        void givenValidPutRequestWithNonexistentDate_shouldReturnError() throws Exception {
            mockMvc.perform(put(URI.create(ENDPOINT))
                    .contentType(APPLICATION_JSON)
                    .content("{\"date\":\"2023-10-10\",\"value\":100.1}")
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(content().string("{\"message\":\"Weight not found for date: 2023-10-10\"}"));
        }

    }

    @Nested
    class Delete {

        @Test
        void givenValidDeleteRequest_shouldDeleteAndReturnNoContent() throws Exception {
            weightRepository.save(weightWithUuid(testUuid));

            mockMvc.perform(delete(URI.create(ENDPOINT + "?date=2023-10-10"))
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

            var result = weightRepository.findAll();
            assertThat(result).hasSize(0);
        }

        @Test
        void givenValidDeleteRequestWithNonexistentDate_shouldReturnError() throws Exception {
            weightRepository.save(weightWithUuid(testUuid));

            mockMvc.perform(delete(URI.create(ENDPOINT + "?date=2023-08-02"))
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(content().string("{\"message\":\"Weight not found for date: 2023-08-02\"}"));

            var result = weightRepository.findAll();
            assertThat(result).hasSize(1);
        }
    }

    private void createUser() {
        User user = user();
        User dbUser = userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest("user", "password");
        token = authenticationService.login(loginRequest).token();
        testUuid = dbUser.getId();
    }

}
