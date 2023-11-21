package com.fittracker.fittracker.dataprovider;

import com.fittracker.fittracker.request.LoginRequest;
import com.fittracker.fittracker.request.RegisterRequest;
import com.fittracker.fittracker.request.WeightRequest;
import java.time.LocalDate;

public class Request {

    public static LoginRequest loginRequest() {
        return new LoginRequest("user", "password");
    }

    public static LoginRequest loginRequestWithUsername(String username) {
        return new LoginRequest(username, "password");
    }

    public static LoginRequest loginRequestWithPassword(String password) {
        return new LoginRequest("user", password);
    }

    public static RegisterRequest registerRequest() {
        return new RegisterRequest("user", "user@example.com", "password");
    }

    public static RegisterRequest registerRequestWithUsername(String username) {
        return new RegisterRequest(username, "user@example.com", "password");
    }

    public static RegisterRequest registerRequestWithEmail(String email) {
        return new RegisterRequest("user", email, "password");
    }

    public static RegisterRequest registerRequestWithPassword(String password) {
        return new RegisterRequest("user", "user@example.com", password);
    }

    public static WeightRequest weightRequest() {
        return new WeightRequest(LocalDate.of(2023, 10, 10), 100.1);
    }

    public static WeightRequest weightRequestWithValue(Double value) {
        return new WeightRequest(LocalDate.of(2023, 10, 10), value);
    }
}
