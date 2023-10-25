package com.fittracker.fittracker.controller;

import com.fittracker.fittracker.request.LoginRequest;
import com.fittracker.fittracker.request.RegisterRequest;
import com.fittracker.fittracker.response.LoginResponse;
import com.fittracker.fittracker.response.RegisterResponse;
import com.fittracker.fittracker.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    //TODO: add validation tests (RegisterRequest and LoginRequest)
    //TODO: add register integration tests: username exists, email exists, happy path
    //TODO: add login integration tests: nonexistent user, wrong password, happy path
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("register")
    public ResponseEntity<RegisterResponse> register(@RequestBody @Valid RegisterRequest registerRequest) {
        return new ResponseEntity<>(authenticationService.register(registerRequest), CREATED);
    }

    @PostMapping("login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        return new ResponseEntity<>(authenticationService.login(loginRequest), OK);
    }
}
