package com.fittracker.fittracker.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class UserDetailsTest {

    @Test
    void fromUser_givenUser_shouldReturnUserDetails() {
        UUID uuid = UUID.fromString("948cc727-68e5-455c-ab6d-942e585bde0d");
        User user = new User(uuid, "user", "user@example.com", "password");

        var expected = new UserDetails(uuid, "user", "password");
        var result = UserDetails.fromUser(user);

        assertThat(result).isEqualTo(expected);
    }
}