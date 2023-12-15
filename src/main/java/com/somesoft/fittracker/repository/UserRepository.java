package com.somesoft.fittracker.repository;

import com.somesoft.fittracker.entity.User;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, String> {

    Optional<User> findByUsername(String username);

    Boolean existsByUsernameOrEmail(String username, String email);

}
