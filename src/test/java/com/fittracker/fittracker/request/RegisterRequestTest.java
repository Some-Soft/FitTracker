package com.fittracker.fittracker.request;

import static com.fittracker.fittracker.dataprovider.Entity.user;
import static com.fittracker.fittracker.dataprovider.Request.registerRequest;
import static com.fittracker.fittracker.dataprovider.TestHelper.compareUpTo;

import org.junit.jupiter.api.Test;

class RegisterRequestTest {

    @Test
    void givenRegisterRequest_toUserShouldReturnUser() {
        RegisterRequest request = registerRequest();

        var expected = user();
        var result = request.toUser();

        compareUpTo(result, expected, "id", "password");
    }
}