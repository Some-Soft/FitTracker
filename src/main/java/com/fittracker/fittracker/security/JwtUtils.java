package com.fittracker.fittracker.security;

import com.fittracker.fittracker.config.JwtConfig;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {

    public static final long MILLISECONDS_PER_MINUTE = 60_000;

    private final long tokenExpirationPeriodMilliseconds;
    private final SecretKey secretKey;

    @Autowired
    public JwtUtils(JwtConfig jwtConfig) {
        this.secretKey = Keys.hmacShaKeyFor(jwtConfig.secret().getBytes());
        this.tokenExpirationPeriodMilliseconds = jwtConfig.tokenExpirationPeriodMinutes() * MILLISECONDS_PER_MINUTE;
    }

    //TODO: add unit test
    public String generateToken(Authentication authentication) {
        Date dateNow = new Date();
        Date expirationDate = new Date(dateNow.getTime() + tokenExpirationPeriodMilliseconds);

        return Jwts.builder()
                .subject(authentication.getName())
                .issuedAt(dateNow)
                .expiration(expirationDate)
                .signWith(secretKey)
                .compact();
    }

    //TODO: add unit test
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    //TODO: add unit test
    public boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
