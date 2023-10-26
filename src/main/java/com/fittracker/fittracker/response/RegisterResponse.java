package com.fittracker.fittracker.response;

import com.fittracker.fittracker.entity.User;

import java.util.UUID;

public record RegisterResponse (UUID id, String username, String email) {

    public static RegisterResponse fromUser(User user) {
        return new RegisterResponse(user.getId(), user.getUsername(), user.getEmail());
    }
}
