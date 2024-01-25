package com.somesoft.fittracker.request;

import static com.somesoft.fittracker.dataprovider.Entity.user;
import static com.somesoft.fittracker.dataprovider.Request.registerRequest;
import static com.somesoft.fittracker.dataprovider.TestHelper.assertEqualRecursiveIgnoring;

import org.junit.jupiter.api.Test;

class RegisterRequestTest {

    @Test
    void givenRegisterRequest_toUserShouldReturnUser() {
        RegisterRequest request = registerRequest();

        var expected = user();
        var result = request.toUser();

        assertEqualRecursiveIgnoring(result, expected, "id", "password");
    }
}