package com.fittracker.fittracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fittracker.fittracker.config.JwtConfig;
import com.fittracker.fittracker.exception.ErrorResponse;
import com.fittracker.fittracker.exception.ErrorResponseMapper;
import com.fittracker.fittracker.request.RegisterRequest;
import com.fittracker.fittracker.security.JwtAuthenticationFilter;
import com.fittracker.fittracker.security.JwtUtils;
import com.fittracker.fittracker.service.AuthenticationService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@Import({ErrorResponseMapper.class, ObjectMapper.class, JwtUtils.class, JwtAuthenticationFilter.class, JwtConfig.class})
class AuthenticationControllerValidationTest {

    private static final String ENDPOINT = "/auth";
    @MockBean
    private AuthenticationService authenticationService;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @Nested
    class Register {

        @ParameterizedTest
        @MethodSource("nullFieldTestDataProvider")
        void givenRegisterRequestWithNullField_shouldReturnErrorResponse(RegisterRequest registerRequest) throws Exception {

            var requestBodyString = mapper.writeValueAsString(registerRequest);

            var responseString = mockMvc
                    .perform(post(URI.create(ENDPOINT + "/register"))
                            .contentType(APPLICATION_JSON)
                            .content(requestBodyString))
                    .andExpect(status().is(BAD_REQUEST.value()))
                    .andReturn().getResponse().getContentAsString();

            var response = mapper.readValue(responseString, ErrorResponse.class);

//            verifyNoInteractions(weightService);
        }

        static List<RegisterRequest> nullFieldTestDataProvider (){
            return List.of(new RegisterRequest(null, "user@example.com", "password"),
                    new RegisterRequest("user", null, "password"),
                    new RegisterRequest("user", "user@example.com", null));
        }

    }


    @Nested
    class Login {


    }


}