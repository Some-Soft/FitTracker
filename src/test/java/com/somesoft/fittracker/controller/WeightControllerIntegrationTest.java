package com.somesoft.fittracker.controller;

import static com.somesoft.fittracker.dataprovider.Entity.weight;
import static com.somesoft.fittracker.dataprovider.Entity.weightWithValue;
import static com.somesoft.fittracker.dataprovider.Request.weightRequest;
import static com.somesoft.fittracker.dataprovider.Request.weightRequestWithValue;
import static com.somesoft.fittracker.dataprovider.Response.weightResponse;
import static com.somesoft.fittracker.dataprovider.Response.weightResponseWithValue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

import com.somesoft.fittracker.entity.Weight;
import com.somesoft.fittracker.exception.ErrorResponse;
import com.somesoft.fittracker.repository.WeightRepository;
import com.somesoft.fittracker.response.WeightResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;

public class WeightControllerIntegrationTest extends BaseIntegrationTest {

    private static final String ENDPOINT = "/api/v1/weight";
    private static final UUID TEST_UUID = UUID.fromString("948cc727-68e5-455c-ab6d-942e585bde0d");

    @Autowired
    private WeightRepository weightRepository;

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
        weightRepository.deleteAll();
    }

    @Nested
    class Post {

        @Test
        void givenValidPostRequest_shouldSaveAnReturnWeight() throws Exception {
            var expectedResponse = weightResponse();
            var expectedWeight = weight();

            var response = makeRequestWithBody(POST, weightRequest(), CREATED, WeightResponse.class);

            assertThat(response).isEqualTo(expectedResponse);
            var allWeights = weightRepository.findAll();
            assertThat(allWeights).hasSize(1);
            assertThat(allWeights.iterator().next()).usingRecursiveComparison().ignoringFields("id")
                .isEqualTo(expectedWeight);

        }

        @Test
        void givenValidPostRequestWithExistingDate_shouldReturnError() throws Exception {
            weightRepository.save(weight());
            var expectedResponse = ErrorResponse.withMessage("Weight already exists for date: 2023-10-10");

            var response = makeRequestWithBody(POST, weightRequest(), BAD_REQUEST, ErrorResponse.class);

            assertThat(response).isEqualTo(expectedResponse);
            assertThat(weightRepository.findAll()).hasSize(1);
        }
    }

    @Nested
    class Get {

        @Nested
        class WeightEndpoint {

            @Test
            void givenValidGetRequestWithExistingDate_shouldReturnWeight() throws Exception {
                weightRepository.save(weight());
                var expectedResponse = weightResponse();

                var response = makeRequest(ENDPOINT + "?date=2023-10-10", GET, OK, WeightResponse.class);

                assertThat(response).isEqualTo(expectedResponse);
            }

            @Test
            void givenValidGetRequestWithNonexistentDate_shouldReturnError() throws Exception {
                var expectedResponse = ErrorResponse.withMessage("Weight not found for date: 2023-10-10");

                var response = makeRequest(ENDPOINT + "?date=2023-10-10", GET, NOT_FOUND, ErrorResponse.class);

                assertThat(response).isEqualTo(expectedResponse);
            }

        }

        @Nested
        class WeightsEndpoint {

            @BeforeEach
            public void beforeEach() {
                List<Weight> weights = List.of(
                    new Weight(LocalDate.of(2023, 1, 1), 100.0, TEST_UUID),
                    new Weight(LocalDate.of(2023, 2, 20), 98.2, TEST_UUID),
                    new Weight(LocalDate.of(2023, 1, 10), 95.5, TEST_UUID)
                );
                weightRepository.saveAll(weights);
            }

            @Test
            void givenDateRange_shouldReturnListOfWeightResponsesSortedByDate() throws Exception {
                var expectedResponse = List.of(
                    new WeightResponse(LocalDate.of(2023, 1, 10), 95.5),
                    new WeightResponse(LocalDate.of(2023, 2, 20), 98.2)
                );

                var response = makeRequest(ENDPOINT + "s?startDate=2023-01-02&endDate=2023-03-04", GET, OK,
                    WeightResponse[].class);

                assertThat(response).containsExactlyElementsOf(expectedResponse);
            }

            @Test
            void givenDateRangeWithNoWeights_shouldReturnEmptyList() throws Exception {
                var response = makeRequest(ENDPOINT + "s?startDate=2023-02-21&endDate=2023-03-04", GET, OK);

                assertThat(response).isEqualTo("[]");
            }

            @Test
            void givenStartDateEqualToEndDateWithWeight_shouldReturnListWithOneElement() throws Exception {
                var expectedResponse = List.of(
                    new WeightResponse(LocalDate.of(2023, 1, 10), 95.5)
                );

                var response = makeRequest(ENDPOINT + "s?startDate=2023-01-10&endDate=2023-01-10", GET, OK,
                    WeightResponse[].class);

                assertThat(response).containsExactlyElementsOf(expectedResponse);
            }

            @Test
            void givenStartDateAfterEndDate_shouldReturnError() throws Exception {
                var expectedResponse = ErrorResponse.withMessage("Start date cannot be after end date");

                var response = makeRequest(ENDPOINT + "s?startDate=2023-01-10&endDate=2023-01-09", GET, BAD_REQUEST,
                    ErrorResponse.class);

                assertThat(response).isEqualTo(expectedResponse);
            }
        }
    }

    @Nested
    class Put {

        @Test
        void givenValidPutRequest_shouldUpdateAndReturnWeightResponse() throws Exception {
            weightRepository.save(weight());
            var expectedResponse = weightResponseWithValue(14.3);
            var expectedWeight = weightWithValue(14.3);

            var response = makeRequestWithBody(PUT, weightRequestWithValue(14.3), OK, WeightResponse.class);

            assertThat(response).isEqualTo(expectedResponse);
            var allWeights = weightRepository.findAll();
            assertThat(allWeights).hasSize(1);
            assertThat(allWeights.iterator().next()).usingRecursiveComparison().ignoringFields("id")
                .isEqualTo(expectedWeight);
        }

        @Test
        void givenValidPutRequestWithNonexistentDate_shouldReturnError() throws Exception {
            var expectedResponse = ErrorResponse.withMessage("Weight not found for date: 2023-10-10");

            var response = makeRequestWithBody(PUT, weightRequest(), NOT_FOUND, ErrorResponse.class);

            assertThat(response).isEqualTo(expectedResponse);
        }
    }

    @Nested
    class Delete {

        @Test
        void givenValidDeleteRequest_shouldDeleteAndReturnNoContent() throws Exception {
            weightRepository.save(weight());

            var response = makeRequest(ENDPOINT + "?date=2023-10-10", DELETE, NO_CONTENT);

            assertThat(response).isEmpty();
            assertThat(weightRepository.findAll()).hasSize(0);
        }

        @Test
        void givenValidDeleteRequestWithNonexistentDate_shouldReturnError() throws Exception {
            weightRepository.save(weight());
            var expectedResponse = ErrorResponse.withMessage("Weight not found for date: 2023-08-02");

            var response = makeRequest(ENDPOINT + "?date=2023-08-02", DELETE, NOT_FOUND, ErrorResponse.class);

            assertThat(response).isEqualTo(expectedResponse);
            assertThat(weightRepository.findAll()).hasSize(1);
        }
    }
}
