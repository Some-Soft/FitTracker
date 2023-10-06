package com.fittracker.fittracker.repository;

import com.fittracker.fittracker.entity.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findByUsername(String username);

    Boolean existsByUsernameOrEmail(String username, String email);

    User save(User user);
}
