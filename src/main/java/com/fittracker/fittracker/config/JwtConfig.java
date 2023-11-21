package com.fittracker.fittracker.config;

public record JwtConfig(String secret, long tokenExpirationPeriodMinutes) {

}
