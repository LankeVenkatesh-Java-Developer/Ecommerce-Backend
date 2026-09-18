package com.venkatesh.it.adminmanagementservice.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private String validToken;
    private String expiredToken;
    private String invalidToken;

    @BeforeEach
    void setUp() {
        String jwtSecret = "mySecretKeyForJWTTokenGenerationWhichShouldBeLongEnough";
        ReflectionTestUtils.setField(jwtService, "jwtSecret", jwtSecret);

        validToken = Jwts.builder()
                .setSubject("123")
                .claim("role", "ADMIN")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .compact();

        expiredToken = Jwts.builder()
                .setSubject("123")
                .claim("role", "ADMIN")
                .setIssuedAt(new Date(System.currentTimeMillis() - 86400000))
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .compact();

        invalidToken = "invalid.token.here";
    }

    @Test
    void whenGetUserIdFromJWT_thenReturnUserId() {
        Long userId = jwtService.getUserIdFromJWT(validToken);

        assertNotNull(userId);
        assertEquals(123L, userId);
    }

    @Test
    void whenGetRoleFromJWT_thenReturnRole() {
        String role = jwtService.getRoleFromJWT(validToken);

        assertNotNull(role);
        assertEquals("ADMIN", role);
    }

    @Test
    void whenValidateTokenWithValidToken_thenReturnTrue() {
        boolean isValid = jwtService.validateToken(validToken);

        assertTrue(isValid);
    }

    @Test
    void whenValidateTokenWithExpiredToken_thenReturnFalse() {
        boolean isValid = jwtService.validateToken(expiredToken);

        assertFalse(isValid);
    }

    @Test
    void whenValidateTokenWithInvalidToken_thenReturnFalse() {
        boolean isValid = jwtService.validateToken(invalidToken);

        assertFalse(isValid);
    }

    @Test
    void whenValidateTokenWithNullToken_thenReturnFalse() {
        boolean isValid = jwtService.validateToken(null);

        assertFalse(isValid);
    }

    @Test
    void whenValidateTokenWithEmptyToken_thenReturnFalse() {
        boolean isValid = jwtService.validateToken("");

        assertFalse(isValid);
    }

    @Test
    void whenValidateTokenWithMalformedToken_thenReturnFalse() {
        String malformedToken = "invalid.token.format";
        boolean isValid = jwtService.validateToken(malformedToken);

        assertFalse(isValid);
    }
}
