package com.fittracker.fittracker.repository;

import com.fittracker.fittracker.entity.User;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
//TODO: remove and use real implementation (e.g. CrudRepository)
public class DummyUserRepository implements UserRepository {

    public Optional<User> findByUsername(String username) {
        if (username.equals("user")) {
            return Optional.of(new User(
                    "3e95e7ba-a135-4c39-a502-54c223f71e20",
                    "user",
                    "user@example.com",
                    "$2y$10$/isqebGTGVApxE/UOXiH3eL1MWrNPf1RMjr/ePgDJU6EFelVfU4Hu"));
        }
        return Optional.empty();
    }

    public Boolean existsByUsernameOrEmail(String username, String email) {
        return username.equals("user") || email.equals("user@example.com");
    }

    public User save(User user) {
        User newUser = new User();
        newUser.setId("3e95e7ba-a135-4c39-a502-54c223f71e20");
        newUser.setUsername("user");
        newUser.setEmail("user@example.com");

        return newUser;
    }
}
