package com.openclassrooms.starterjwt.Security.jwt;

import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import org.junit.jupiter.api.DisplayName;

// =================================================================
// Test class for JwtUtils
// Tests JWT token generation, validation and user extraction
// =================================================================

@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {

    // =================================================================
    // Inject required components and mocks
    // =================================================================

    @InjectMocks
    private JwtUtils jwtUtils;

    @Mock
    private Authentication authentication;

    private UserDetailsImpl userDetails;
    private final String jwtSecret = "openclassroomstoken";
    private final int jwtExpirationMs = 86400000; // 24 hours

    // =================================================================
    // Initialize test environment
    // =================================================================

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", jwtSecret);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", jwtExpirationMs);

        userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@test.com")
                .firstName("Test")
                .lastName("User")
                .password("password")
                .build();
    }

    // =================================================================
    // Test comprehensive JWT token generation and validation scenarios
    // =================================================================
    @Test
    @DisplayName("Should handle all JWT token scenarios correctly")
    void jwtToken_AllScenarios_ShouldBehaveProperly() {

        // =================================================================
        // Arrange - Setup test cases for invalid tokens
        // =================================================================
        String invalidSignatureToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWxNTE2MjM5MDIyLCJleHAiOjE1MTYyMzkwMjJ9.invalid_signature";
        String malformedToken = "malformed.token.here";
        String emptyToken = "";

        // =================================================================
        // Setup expired token
        // =================================================================
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", -86400000); // Set expiration to -24 hours
        when(authentication.getPrincipal()).thenReturn(userDetails);
        String expiredToken = jwtUtils.generateJwtToken(authentication);

        // =================================================================
        // Setup for valid token generation
        // =================================================================
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 86400000); // Reset to +24 hours
        Date beforeToken = new Date();

        // =================================================================
        // Act - Generate valid token and extract data
        // =================================================================

        String validToken = jwtUtils.generateJwtToken(authentication);
        String username = jwtUtils.getUserNameFromJwtToken(validToken);
        Date expiration = io.jsonwebtoken.Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(validToken)
                .getBody()
                .getExpiration();

        // =================================================================
        // Assert - Verify all scenarios
        // =================================================================
        assertAll(
                "Complete JWT token validation suite",

                // =================================================================
                // Invalid token scenarios
                // =================================================================
                () -> assertFalse(jwtUtils.validateJwtToken(invalidSignatureToken),
                        "Invalid signature token should be rejected"),
                () -> assertFalse(jwtUtils.validateJwtToken(malformedToken),
                        "Malformed token should be rejected"),
                () -> assertFalse(jwtUtils.validateJwtToken(emptyToken),
                        "Empty token should be rejected"),
                () -> assertFalse(jwtUtils.validateJwtToken(expiredToken),
                        "Expired token should be rejected"),

                // =================================================================
                // Valid token scenarios
                // =================================================================
                () -> assertNotNull(validToken,
                        "Generated token should not be null"),
                () -> assertTrue(jwtUtils.validateJwtToken(validToken),
                        "Generated token should be valid"),
                () -> assertEquals("test@test.com", username,
                        "Token should contain correct username"),
                () -> assertTrue(Math.abs(expiration.getTime() - beforeToken.getTime() - 86400000) < 1000,
                        "Token should have correct expiration time with 1 second tolerance"));
    }
}