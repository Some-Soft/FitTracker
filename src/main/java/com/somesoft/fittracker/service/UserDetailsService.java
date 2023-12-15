package com.somesoft.fittracker.service;

import static java.lang.String.format;

import com.somesoft.fittracker.entity.User;
import com.somesoft.fittracker.entity.UserDetails;
import com.somesoft.fittracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private static final String EXCEPTION_MESSAGE_TEMPLATE = "Username %s not found";

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException(format(EXCEPTION_MESSAGE_TEMPLATE, username)));

        return UserDetails.fromUser(user);
    }
}
