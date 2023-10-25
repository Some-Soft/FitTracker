package com.fittracker.fittracker.response;

import com.fittracker.fittracker.entity.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterResponseTest {

    @Test
    void fromUser() {
        String exampleUserName = "exampleUserName";
        String exampleEmail = "user@example.com";
        String examplePassword = "examplePassword";
        String exampleId = "510e2500-e29b-41d4-a716-447655440000";
        User user = new User(exampleId,exampleUserName,exampleEmail,examplePassword);

        var expected = new RegisterResponse(exampleId,exampleUserName,exampleEmail);
        var result = RegisterResponse.fromUser(user);

        assertThat(result).isEqualTo(expected);
    }
}