package com.fittracker.fittracker.controller;


import com.fittracker.fittracker.exception.ErrorResponseMapper;
import com.fittracker.fittracker.service.WeightService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
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

import static org.junit.jupiter.params.provider.Arguments.of;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
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

    private static Stream<Arguments> postRequestTestDataProvider() {

        return Stream.of(
                of(CREATED, "{\"date\":\"2023-08-03\",\"value\":13.2}", "", times(1)),
                of(CREATED, "{\"date\":\"" + LocalDate.now() + "\",\"value\":13.2}", "", times(1)),
                of(BAD_REQUEST, "{\"date\":\"202-08-03\",\"value\":13.2}",
                        "{\"field\":\"date\",\"message\":\"Date must be in format: YYYY-MM-DD\"}", never()),
                of(BAD_REQUEST, "{\"date\":\"03-08-2023\",\"value\":13.2}",
                        "{\"field\":\"date\",\"message\":\"Date must be in format: YYYY-MM-DD\"}", never()),
                of(BAD_REQUEST, "{\"value\":13.2}",
                        "{\"field\":\"date\",\"message\":\"Date must not be null\"}", never()),
                of(BAD_REQUEST, "{\"date\":\"" + LocalDate.of(3000,1,1) + "\",\"value\":13.2}",
                        "{\"field\":\"date\",\"message\":\"Date cannot be in the future\"}", never()),
                of(BAD_REQUEST, "{\"date\":\"2021-01-02\",\"value\":13.2}",
                        "{\"field\":\"date\",\"message\":\"Date must be after 2022\"}", never()),
                of(BAD_REQUEST, "{\"date\":\"2022-12-31\",\"value\":13.2}",
                        "{\"field\":\"date\",\"message\":\"Date must be after 2022\"}", never()),
                of(BAD_REQUEST, "{\"date\":\"2023-08-03\"}",
                        "{\"field\":\"value\",\"message\":\"Value must not be null\"}", never()),
                of(BAD_REQUEST, "{\"date\":\"2023-08-03\",\"value\":-1}",
                        "{\"field\":\"value\",\"message\":\"Value must be positive\"}", never()),
                of(BAD_REQUEST, "{\"date\":\"2023-08-03\",\"value\":700}",
                        "{\"field\":\"value\",\"message\":\"Value must be less than 635\"}", never())
        );

    }

    @ParameterizedTest
    @MethodSource("postRequestTestDataProvider")
    void givenUriPostRequest_shouldReturnResponse(HttpStatus responseStatus, String requestBody, String responseBody, VerificationMode numberOfServiceInvocations) throws Exception {
        mockMvc
                .perform(post(URI.create(ENDPOINT))
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().is(responseStatus.value()))
                .andExpect(content().string(responseBody));

        verify(weightService, numberOfServiceInvocations).save(any());
    }


}
