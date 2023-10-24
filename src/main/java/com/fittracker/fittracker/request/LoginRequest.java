package com.fittracker.fittracker.request;

//TODO: add validation: username, password - not null
public record LoginRequest(String username, String password) {
}
