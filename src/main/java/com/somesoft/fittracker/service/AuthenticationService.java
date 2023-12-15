package com.somesoft.fittracker.service;

import com.somesoft.fittracker.entity.User;
import com.somesoft.fittracker.exception.UserAlreadyExistsException;
import com.somesoft.fittracker.repository.UserRepository;
import com.somesoft.fittracker.request.LoginRequest;
import com.somesoft.fittracker.request.RegisterRequest;
import com.somesoft.fittracker.response.LoginResponse;
import com.somesoft.fittracker.response.RegisterResponse;
import com.somesoft.fittracker.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Autowired
    public AuthenticationService(AuthenticationManager authenticationManager, JwtUtils jwtUtils,
        PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public RegisterResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByUsernameOrEmail(registerRequest.username(), registerRequest.email())) {
            throw new UserAlreadyExistsException(registerRequest);
        }
        User user = registerRequest.toUser();
        user.setPassword(passwordEncoder.encode(registerRequest.password()));

        return RegisterResponse.fromUser(userRepository.save(user));
    }

    public LoginResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
            loginRequest.username(), loginRequest.password()));

        return new LoginResponse(jwtUtils.generateToken(authentication));
    }
}
