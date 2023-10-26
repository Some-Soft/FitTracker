package com.fittracker.fittracker.repository;

import com.fittracker.fittracker.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User,String> {

    Optional<User> findByUsername(String username);

    Boolean existsByUsernameOrEmail(String username, String email);

}
