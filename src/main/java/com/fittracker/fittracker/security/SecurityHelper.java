package com.fittracker.fittracker.security;

import java.util.UUID;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityHelper {

    public static UUID getUserId() {
        return (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
