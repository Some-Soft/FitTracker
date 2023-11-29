package com.fittracker.fittracker.dataprovider;

import com.fittracker.fittracker.request.LoginRequest;
import com.fittracker.fittracker.request.ProductRequest;
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

    public static ProductRequest productRequest() {
        return new ProductRequest("bread", 245, 58, 8, 0);
    }

    public static ProductRequest productRequestWithName(String name) {
        return new ProductRequest(name, 245, 58, 8, 0);
    }

    public static ProductRequest productRequestWithKcal(int kcal) {
        return new ProductRequest("bread", kcal, 58, 8, 0);
    }

    public static ProductRequest productRequestWithCarbs(Integer carbs) {
        return new ProductRequest("bread", 245, carbs, 8, 0);
    }

    public static ProductRequest productRequestWithProtein(int protein) {
        return new ProductRequest("bread", 245, 58, protein, 0);
    }

    public static ProductRequest productRequestWithFat(int fat) {
        return new ProductRequest("bread", 245, 58, 8, fat);
    }


}
