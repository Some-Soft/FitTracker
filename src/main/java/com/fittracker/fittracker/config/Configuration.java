package com.fittracker.fittracker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class Configuration {


    @Bean
    public JwtConfig jwtConfig(@Value("${security.jwt.secret}") String secret,
                               @Value("${security.jwt.tokenExpirationPeriodMinutes}") String tokenExpirationPeriodMinutes) {
        return new JwtConfig(secret, Integer.parseInt(tokenExpirationPeriodMinutes));
    }
}
