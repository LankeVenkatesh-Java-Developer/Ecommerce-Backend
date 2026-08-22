package com.ashok.it.userservice.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtService Tests")
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private static final String TEST_SECRET = "testSecretKeyForUnitTesting123456789";
    private static final long TEST_EXPIRATION = 3600000L;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtService, "expiration", TEST_EXPIRATION);
    }

    @Nested
    @DisplayName("generateToken() Tests")
    class GenerateTokenTests {

        @Test
        @DisplayName("Should generate valid JWT token")
        void shouldGenerateValidToken() {
            // Arrange
            Long userId = 1L;
            String email = "john.doe@example.com";

            // Act
            String token = jwtService.generateToken(userId, email);

            // Assert
            assertThat(token).isNotNull();
            assertThat(token).isNotEmpty();
            assertThat(token.split("\\.")).hasSize(3); // JWT has 3 parts
        }

        @Test
        @DisplayName("Should generate different tokens for different users")
        void shouldGenerateDifferentTokensForDifferentUsers() {
            // Arrange
            Long userId1 = 1L;
            Long userId2 = 2L;
            String email1 = "john.doe@example.com";
            String email2 = "jane.smith@example.com";

            // Act
            String token1 = jwtService.generateToken(userId1, email1);
            String token2 = jwtService.generateToken(userId2, email2);

            // Assert
            assertThat(token1).isNotEqualTo(token2);
        }

        @Test
        @DisplayName("Should generate token with correct email in subject")
        void shouldGenerateTokenWithCorrectEmail() {
            // Arrange
            Long userId = 1L;
            String email = "test.user@example.com";

            // Act
            String token = jwtService.generateToken(userId, email);
            String extractedEmail = jwtService.extractEmail(token);

            // Assert
            assertThat(extractedEmail).isEqualTo(email);
        }

        @Test
        @DisplayName("Should generate token with userId claim")
        void shouldGenerateTokenWithUserIdClaim() {
            // Arrange
            Long userId = 123L;
            String email = "user@example.com";

            // Act
            String token = jwtService.generateToken(userId, email);
            Long extractedUserId = jwtService.extractClaim(token, claims -> claims.get("userId", Long.class));

            // Assert
            assertThat(extractedUserId).isEqualTo(userId);
        }
    }

    @Nested
    @DisplayName("extractEmail() Tests")
    class ExtractEmailTests {

        @Test
        @DisplayName("Should extract email from valid token")
        void shouldExtractEmailFromValidToken() {
            // Arrange
            Long userId = 1L;
            String email = "extract.test@example.com";
            String token = jwtService.generateToken(userId, email);

            // Act
            String extractedEmail = jwtService.extractEmail(token);

            // Assert
            assertThat(extractedEmail).isEqualTo(email);
        }

        @Test
        @DisplayName("Should extract email with special characters")
        void shouldExtractEmailWithSpecialCharacters() {
            // Arrange
            Long userId = 1L;
            String email = "user+tag@example-domain.com";
            String token = jwtService.generateToken(userId, email);

            // Act
            String extractedEmail = jwtService.extractEmail(token);

            // Assert
            assertThat(extractedEmail).isEqualTo(email);
        }
    }

    @Nested
    @DisplayName("extractClaim() Tests")
    class ExtractClaimTests {

        @Test
        @DisplayName("Should extract userId claim from token")
        void shouldExtractUserIdClaim() {
            // Arrange
            Long userId = 999L;
            String email = "claim.test@example.com";
            String token = jwtService.generateToken(userId, email);

            // Act
            Long extractedUserId = jwtService.extractClaim(token, claims -> claims.get("userId", Long.class));

            // Assert
            assertThat(extractedUserId).isEqualTo(userId);
        }

        @Test
        @DisplayName("Should extract issuedAt claim from token")
        void shouldExtractIssuedAtClaim() {
            // Arrange
            Long userId = 1L;
            String email = "issued.at@example.com";
            String token = jwtService.generateToken(userId, email);

            // Act
            Long issuedAt = jwtService.extractClaim(token, claims -> claims.getIssuedAt().getTime());

            // Assert
            assertThat(issuedAt).isPositive();
            assertThat(issuedAt).isLessThanOrEqualTo(System.currentTimeMillis());
        }

        @Test
        @DisplayName("Should extract expiration claim from token")
        void shouldExtractExpirationClaim() {
            // Arrange
            Long userId = 1L;
            String email = "expiration@example.com";
            String token = jwtService.generateToken(userId, email);

            // Act
            Long expiration = jwtService.extractClaim(token, claims -> claims.getExpiration().getTime());

            // Assert
            assertThat(expiration).isPositive();
            assertThat(expiration).isGreaterThan(System.currentTimeMillis());
        }
    }

    @Nested
    @DisplayName("isTokenValid() Tests")
    class IsTokenValidTests {

        @Test
        @DisplayName("Should validate token with correct email")
        void shouldValidateTokenWithCorrectEmail() {
            // Arrange
            Long userId = 1L;
            String email = "valid@example.com";
            String token = jwtService.generateToken(userId, email);

            // Act
            boolean isValid = jwtService.isTokenValid(token, email);

            // Assert
            assertThat(isValid).isTrue();
        }

        @Test
        @DisplayName("Should invalidate token with wrong email")
        void shouldInvalidateTokenWithWrongEmail() {
            // Arrange
            Long userId = 1L;
            String email = "original@example.com";
            String wrongEmail = "wrong@example.com";
            String token = jwtService.generateToken(userId, email);

            // Act
            boolean isValid = jwtService.isTokenValid(token, wrongEmail);

            // Assert
            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("Should validate non-expired token")
        void shouldValidateNonExpiredToken() {
            // Arrange
            Long userId = 1L;
            String email = "nonexpired@example.com";
            String token = jwtService.generateToken(userId, email);

            // Act
            boolean isValid = jwtService.isTokenValid(token, email);

            // Assert
            assertThat(isValid).isTrue();
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should generate, extract, and validate token end-to-end")
        void shouldGenerateExtractAndValidateToken() {
            // Arrange
            Long userId = 42L;
            String email = "integration@example.com";

            // Act
            String token = jwtService.generateToken(userId, email);
            String extractedEmail = jwtService.extractEmail(token);
            Long extractedUserId = jwtService.extractClaim(token, claims -> claims.get("userId", Long.class));
            boolean isValid = jwtService.isTokenValid(token, email);

            // Assert
            assertThat(extractedEmail).isEqualTo(email);
            assertThat(extractedUserId).isEqualTo(userId);
            assertThat(isValid).isTrue();
        }

        @Test
        @DisplayName("Should handle case-sensitive email validation")
        void shouldHandleCaseSensitiveEmail() {
            // Arrange
            Long userId = 1L;
            String email = "CaseSensitive@example.com";
            String token = jwtService.generateToken(userId, email);

            // Act
            boolean isValidSameCase = jwtService.isTokenValid(token, "CaseSensitive@example.com");
            boolean isValidDifferentCase = jwtService.isTokenValid(token, "casesensitive@example.com");

            // Assert
            assertThat(isValidSameCase).isTrue();
            assertThat(isValidDifferentCase).isFalse();
        }
    }
}
