package com.fittracker.fittracker.request;

import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @NotNull(message = "Username must not be null")
        String username,
        @NotNull(message = "Password must not be null")
        String password) {
}
