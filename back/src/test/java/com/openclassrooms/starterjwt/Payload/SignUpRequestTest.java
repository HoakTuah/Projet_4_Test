package com.openclassrooms.starterjwt.Payload;

import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

// =================================================================
// Tests for SignupRequest DTO
// Verify validation rules for user registration data
// Tests all constraints (@NotBlank, @Size, @Email) and data integrity
// ================================================================= 

class SignupRequestTest {

    private static Validator validator;

    // =================================================================
    // Initialize the validator for all tests
    // Used to validate bean constraints (@NotBlank, @Size, @Email)
    // =================================================================

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // =================================================================
    // Test for valid signup data
    // =================================================================

    @Test
    @DisplayName("Should accept valid signup data")
    void testValidSignupRequest() {

        // =================================================================
        // Arrange - Create request with valid data
        // =================================================================

        SignupRequest request = new SignupRequest();
        request.setEmail("test@test.com");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPassword("password123");

        // =================================================================
        // Act - Validate the request
        // =================================================================

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);

        // =================================================================
        // Assert - Verify data integrity and no violations
        // =================================================================
        assertTrue(violations.isEmpty());
        assertEquals("test@test.com", request.getEmail());
        assertEquals("John", request.getFirstName());
        assertEquals("Doe", request.getLastName());
        assertEquals("password123", request.getPassword());
    }

    // =================================================================
    // Test for invalid email format
    // =================================================================

    @Test
    @DisplayName("Should reject invalid email format")
    void testInvalidEmail() {

        // =================================================================
        // Arrange - Create request with invalid email
        // =================================================================

        SignupRequest request = new SignupRequest();
        request.setEmail("invalid-email");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPassword("password123");

        // =================================================================
        // Act - Validate the request
        // =================================================================
        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);

        // =================================================================
        // Assert - Verify email validation failure
        // =================================================================

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    // =================================================================
    // Test for password too short
    // =================================================================

    @Test
    @DisplayName("Should reject password too short")
    void testPasswordTooShort() {

        // =================================================================
        // Arrange - Create request with short password
        // =================================================================

        SignupRequest request = new SignupRequest();
        request.setEmail("test@test.com");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPassword("12345"); // 5 characters

        // =================================================================
        // Act - Validate the request
        // =================================================================

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);

        // =================================================================
        // Assert - Verify password validation failure
        // =================================================================

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }

    // =================================================================
    // Test for equals and hashCode
    // =================================================================

    @Test
    @DisplayName("Should correctly implement equals and hashCode")
    void testEqualsAndHashCode() {

        // =================================================================
        // Arrange - Create two equal requests
        // =================================================================

        SignupRequest request1 = new SignupRequest();
        request1.setEmail("test@test.com");
        request1.setFirstName("John");
        request1.setLastName("Doe");
        request1.setPassword("password123");

        SignupRequest request2 = new SignupRequest();
        request2.setEmail("test@test.com");
        request2.setFirstName("John");
        request2.setLastName("Doe");
        request2.setPassword("password123");

        // =================================================================
        // Assert - Verify equality and hash code
        // =================================================================

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
    }
}