package com.fittracker.fittracker.controller;


import com.fittracker.fittracker.exception.ErrorResponseMapper;
import com.fittracker.fittracker.service.WeightService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
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


    @ParameterizedTest
    @EnumSource(GetRequestTestData.class)
    void givenGetUriRequest_shouldReturnResponse(GetRequestTestData testData) throws Exception {

        mockMvc
                .perform(get(URI.create(ENDPOINT + testData.getParams())))
                .andExpect(status().is(testData.getResponseStatus().value()))
                .andExpect(content().string(testData.getResponseBody()));

        verify(weightService, testData.getNumberOfServiceInvocations()).findByDate(any());
    }

    @ParameterizedTest
    @EnumSource(PostRequestTestData.class)
    void givenUriPostRequest_shouldReturnResponse(PostRequestTestData testData) throws Exception {
        mockMvc
                .perform(post(URI.create(ENDPOINT))
                        .contentType("application/json")
                        .content(testData.getRequestBody()))
                .andExpect(status().is(testData.getResponseStatus().value()))
                .andExpect(content().string(testData.getResponseBody()));

        verify(weightService, testData.getNumberOfServiceInvocations()).save(any());
    }


}
