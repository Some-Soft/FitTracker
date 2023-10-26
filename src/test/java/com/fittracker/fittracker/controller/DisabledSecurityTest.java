package com.fittracker.fittracker.controller;

import com.fittracker.fittracker.config.JwtConfig;
import com.fittracker.fittracker.security.JwtAuthenticationFilter;
import com.fittracker.fittracker.security.JwtUtils;
import org.springframework.boot.test.mock.mockito.MockBean;

public abstract class DisabledSecurityTest {
    @MockBean
    JwtUtils jwtUtils;
    @MockBean
    JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean
    JwtConfig jwtConfig;
}
