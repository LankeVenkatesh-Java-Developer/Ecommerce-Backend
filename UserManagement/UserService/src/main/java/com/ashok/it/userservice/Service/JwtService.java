package com.ashok.it.userservice.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Service
@Slf4j
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey getSigningKey() {
        log.debug("Creating signing key with secret length: {}", secret != null ? secret.length() : 0);
        if (secret == null || secret.length() < 32) {
            log.error("JWT secret key is too short or null. Minimum 32 characters required.");
            throw new IllegalArgumentException("JWT secret key must be at least 32 characters long");
        }
        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateToken(
            Long userId,
            String email) {

        log.info("Generating token for user ID: {}, email: {}", userId, email);
        Date now = new Date();

        Date expiry =
                new Date(now.getTime() + expiration);

        try {
            String token = Jwts.builder()
                    .subject(email)
                    .claim("userId", userId)
                    .issuedAt(now)
                    .expiration(expiry)
                    .signWith(getSigningKey())
                    .compact();
            log.info("Token generated successfully for user ID: {}", userId);
            return token;
        } catch (Exception e) {
            log.error("Failed to generate token for user ID: {}, email: {}", userId, email, e);
            throw new RuntimeException("Token generation failed", e);
        }
    }

    public String extractEmail(String token) {

        return extractClaim(
                token,
                Claims::getSubject
        );
    }

    public <T> T extractClaim(
            String token,
            Function<Claims, T> resolver) {

        Claims claims =
                Jwts.parser()
                        .verifyWith(getSigningKey())
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

        return resolver.apply(claims);
    }

    public boolean isTokenValid(
            String token,
            String email) {

        String tokenEmail =
                extractEmail(token);

        Date expiration =
                extractClaim(
                        token,
                        Claims::getExpiration
                );

        return tokenEmail.equals(email)
                && expiration.after(new Date());
    }
}