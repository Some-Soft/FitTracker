package com.fittracker.fittracker.security;

import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public class SecurityHelper {

    public static UUID getUserId() {
        return (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
