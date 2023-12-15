package com.somesoft.fittracker.config;

public record JwtConfig(String secret, long tokenExpirationPeriodMinutes) {

}
