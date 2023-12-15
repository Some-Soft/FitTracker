package com.somesoft.fittracker.security;

import com.somesoft.fittracker.config.JwtConfig;
import com.somesoft.fittracker.entity.UserDetails;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {

    private static final int MILLISECONDS_PER_MINUTE = 60_000;

    private final long tokenExpirationPeriodMilliseconds;
    private final SecretKey secretKey;

    @Autowired
    public JwtUtils(JwtConfig jwtConfig) {
        this.secretKey = Keys.hmacShaKeyFor(jwtConfig.secret().getBytes());
        this.tokenExpirationPeriodMilliseconds = jwtConfig.tokenExpirationPeriodMinutes() * MILLISECONDS_PER_MINUTE;
    }


    public String generateToken(Authentication authentication) {
        Date dateNow = new Date();
        Date expirationDate = new Date(dateNow.getTime() + tokenExpirationPeriodMilliseconds);

        return Jwts.builder()
            .claim("userId", ((UserDetails) authentication.getPrincipal()).id())
            .subject(authentication.getName())
            .issuedAt(dateNow)
            .expiration(expirationDate)
            .signWith(secretKey)
            .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }

    public UUID getUserIdFromToken(String token) {
        return UUID.fromString(Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .get("userId", String.class));
    }


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
