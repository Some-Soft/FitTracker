package com.fittracker.fittracker.controller;

import com.fittracker.fittracker.exception.ErrorResponseMapper;
import com.fittracker.fittracker.service.AuthenticationService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

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


    }

    @Nested
    class Login {


    }


}