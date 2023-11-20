package com.fittracker.fittracker.request;

import com.fittracker.fittracker.entity.User;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record RegisterRequest(
    @NotNull(message = "Username must not be null")
    @Length(min = 3, max = 64, message = "Username must be between 3 and 64 characters")
    String username,
    @NotNull(message = "Email must not be null")
    @Length(min = 3, max = 254, message = "Email must be between 3 and 254 characters")
    String email,
    @NotNull(message = "Password must not be null")
    @Length(min = 3, max = 30, message = "Password must be between 3 and 30 characters")
    String password
) {

    public User toUser() {
        return new User(this.username, this.email);
    }
}
