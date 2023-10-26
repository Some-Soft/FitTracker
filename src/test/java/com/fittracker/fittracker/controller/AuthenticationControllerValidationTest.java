package com.fittracker.fittracker.controller;

import com.fittracker.fittracker.exception.ErrorResponseMapper;
import com.fittracker.fittracker.service.AuthenticationService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WeightController.class)
@Import(ErrorResponseMapper.class)
class AuthenticationControllerValidationTest {

    private static final String ENDPOINT = "/auth";
    @MockBean
    private AuthenticationService authenticationService;

    @Autowired
    private MockMvc mockMvc;

    @Nested
    class Register {

        @ParameterizedTest
        @CsvSource({
                "",
                ""
        })
        void givenRegisterRequestWithNullField_shouldReturnErrorResponse(String field) {
            mockMvc
                    .perform(post(URI.create(ENDPOINT + "/register"))
                            .contentType(APPLICATION_JSON)
                            .content(format("{\"date\":\"2023-08-03\",\"value\": %s}", value)))
                    .andExpect(status().is(BAD_REQUEST.value()))
                    .andExpect(content().string(format("{\"field\":\"value\",\"message\":\"%s\"}", message)));

            verifyNoInteractions(weightService);
        }

        private String generate
    }



    @Nested
    class Login {


    }


}