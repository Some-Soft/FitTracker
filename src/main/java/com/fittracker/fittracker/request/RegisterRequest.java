package com.fittracker.fittracker.request;

import com.fittracker.fittracker.entity.User;

//TODO: add validation: username - not null, at least 3 characters, max 64 characters
//TODO: add validation: email - not null, at least 3 characters, max 254 characters
//TODO: add validation: password - not null, at least 8 characters, max 30 characters
public record RegisterRequest (String username, String email, String password) {
    //TODO: add unit test
    public User toUser() {
        return new User(this.username, this.email);
    }
}
