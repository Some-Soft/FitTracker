package com.fittracker.fittracker.controller;

import com.fittracker.fittracker.request.LoginRequest;
import com.fittracker.fittracker.request.RegisterRequest;
import com.fittracker.fittracker.response.LoginResponse;
import com.fittracker.fittracker.response.RegisterResponse;
import com.fittracker.fittracker.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest registerRequest) {
        //TODO: handle UserAlreadyExistsException
        return new ResponseEntity<>(authenticationService.register(registerRequest), HttpStatus.CREATED);
    }

    @PostMapping("login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return new ResponseEntity<>(authenticationService.login(loginRequest), HttpStatus.OK);
    }
}
