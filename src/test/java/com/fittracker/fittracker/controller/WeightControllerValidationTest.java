package com.fittracker.fittracker.controller;


import com.fittracker.fittracker.exception.ErrorResponseMapper;
import com.fittracker.fittracker.service.WeightService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.verification.VerificationMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.time.LocalDate;
import java.util.stream.Stream;

import static java.lang.String.format;
import static org.junit.jupiter.params.provider.Arguments.of;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WeightController.class)
@Import(ErrorResponseMapper.class)
public class WeightControllerValidationTest {

    @MockBean
    private WeightService weightService;

    @Autowired
    private MockMvc mockMvc;

    private static final String ENDPOINT = "/api/v1/weight";

    @Nested
    class Get {
        private static Stream<Arguments> getRequestTestDataProvider() {
            return Stream.of(
                    of("", BAD_REQUEST, "{\"field\":\"date\",\"message\":\"Parameter must not be null\"}", never()),
                    of("?date=123", BAD_REQUEST, "{\"field\":\"date\",\"message\":\"Invalid data type\"}", never()),
                    of("?date=2020-01-01", OK, "", times(1)),
                    of("?date=2050-10-10", OK, "", times(1))
            );
        }

        @ParameterizedTest
        @MethodSource("getRequestTestDataProvider")
        void givenGetUriRequest_shouldReturnResponse(String params, HttpStatus responseStatus, String responseBody, VerificationMode numberOfServiceInvocations) throws Exception {
            mockMvc
                    .perform(get(URI.create(ENDPOINT + params)))
                    .andExpect(status().is(responseStatus.value()))
                    .andExpect(content().string(responseBody));

            verify(weightService, numberOfServiceInvocations).findByDate(any());
        }
    }

    @Nested
    class Post {
        @ParameterizedTest
        @CsvSource({
                "202-08-03, Date must be in format: YYYY-MM-DD",
                "03-08-2023, Date must be in format: YYYY-MM-DD",
                "3000-01-01, Date cannot be in the future",
                "2021-01-02, Date must be after 2022",
                "2022-12-31, Date must be after 2022"
        })
        void givenPostRequest_shouldReturnDateErrorMessage(String date, String message) throws Exception {
            mockMvc
                    .perform(post(URI.create(ENDPOINT))
                            .contentType(APPLICATION_JSON)
                            .content(format("{\"date\":\"%s\",\"value\": 13.2}", date)))
                    .andExpect(status().is(BAD_REQUEST.value()))
                    .andExpect(content().string(format("{\"field\":\"date\",\"message\":\"%s\"}", message)));

            verifyNoInteractions(weightService);
        }

        @Test
        public void givenPostRequestWithValidDate_shouldReturnCreated() throws Exception {
            mockMvc
                    .perform(post(URI.create(ENDPOINT))
                            .contentType("application/json")
                            .content("{\"date\":\"2023-10-10\",\"value\": 13.2}"))
                    .andExpect(status().is(CREATED.value()))
                    .andExpect(content().string(""));

            verify(weightService).save(any());
        }

        @Test
        public void givenPostRequestWithDateNow_shouldReturnCreated() throws Exception {
            mockMvc
                    .perform(post(URI.create(ENDPOINT))
                            .contentType("application/json")
                            .content(format("{\"date\":\"%s\",\"value\": 13.2}", LocalDate.now())))
                    .andExpect(status().is(CREATED.value()))
                    .andExpect(content().string(""));

            verify(weightService).save(any());
        }

        @Test
        public void givenPostRequestWithNullDate_shouldReturnDateErrorMessage() throws Exception {
            mockMvc
                    .perform(post(URI.create(ENDPOINT))
                            .contentType("application/json")
                            .content("{\"value\": 13.2}"))
                    .andExpect(status().is(BAD_REQUEST.value()))
                    .andExpect(content().string("{\"field\":\"date\",\"message\":\"Date must not be null\"}"));

            verifyNoInteractions(weightService);
        }



        //TODO: similar tests for value
//    of(BAD_REQUEST, "{\"date\":\"2023-08-03\"}",
//            "{\"field\":\"value\",\"message\":\"Value must not be null\"}", never()),
//    of(BAD_REQUEST, "{\"date\":\"2023-08-03\",\"value\":-1}",
//            "{\"field\":\"value\",\"message\":\"Value must be positive\"}", never()),
//    of(BAD_REQUEST, "{\"date\":\"2023-08-03\",\"value\":700}",
//            "{\"field\":\"value\",\"message\":\"Value must be less than 635\"}", never())

    }
}
