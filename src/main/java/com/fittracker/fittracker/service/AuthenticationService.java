package com.fittracker.fittracker.service;

import com.fittracker.fittracker.entity.User;
import com.fittracker.fittracker.exception.UserAlreadyExistsException;
import com.fittracker.fittracker.repository.UserRepository;
import com.fittracker.fittracker.request.LoginRequest;
import com.fittracker.fittracker.request.RegisterRequest;
import com.fittracker.fittracker.response.LoginResponse;
import com.fittracker.fittracker.response.RegisterResponse;
import com.fittracker.fittracker.security.JwtUtils;
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

    //TODO: add unit test
    public RegisterResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByUsernameOrEmail(registerRequest.username(), registerRequest.email())) {
            throw new UserAlreadyExistsException(registerRequest);
        }
        User user = registerRequest.toUser();
        user.setPassword(passwordEncoder.encode(registerRequest.username()));

        return RegisterResponse.fromUser(userRepository.save(user));
    }

    //TODO: add unit test
    public LoginResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.username(), loginRequest.password()));

        return new LoginResponse(jwtUtils.generateToken(authentication));
    }
}
