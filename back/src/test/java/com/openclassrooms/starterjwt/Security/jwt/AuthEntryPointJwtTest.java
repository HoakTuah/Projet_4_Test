package com.openclassrooms.starterjwt.Security.jwt;

import com.openclassrooms.starterjwt.security.jwt.AuthEntryPointJwt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.MediaType;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

// =================================================================
// Integration test class for AuthEntryPointJwt
// Uses full Spring context for testing
// =================================================================
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Auth Entry Point JWT Integration Tests")
public class AuthEntryPointJwtTest {

    // =================================================================
    // Inject required components
    // =================================================================
    @Autowired
    private AuthEntryPointJwt authEntryPointJwt;

    // =================================================================
    // Define test data
    // =================================================================
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private AuthenticationException authException;

    @BeforeEach
    void setUp() {
        // =================================================================
        // Initialize test objects
        // =================================================================
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        authException = new AuthenticationException("Unauthorized error") {
            private static final long serialVersionUID = 1L;
        };

        request.setServletPath("/api/test");
    }

    // =================================================================
    // Test unauthorized access handling
    // =================================================================
    @Test
    @DisplayName("Should handle unauthorized access correctly")
    void commence_UnauthorizedAccess() throws IOException, ServletException {
        // =================================================================
        // Act - Trigger unauthorized access
        // =================================================================
        authEntryPointJwt.commence(request, response, authException);

        // =================================================================
        // Assert - Verify response properties
        // =================================================================
        assertEquals(401, response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        String content = response.getContentAsString();
        assertTrue(content.contains("\"status\":401"));
        assertTrue(content.contains("\"error\":\"Unauthorized\""));
        assertTrue(content.contains("\"path\":\"/api/test\""));
    }

    // =================================================================
    // Test error message in response
    // =================================================================
    @Test
    @DisplayName("Should include error message in response")
    void commence_ErrorMessage() throws IOException, ServletException {
        // =================================================================
        // Arrange - Set up custom error message
        // =================================================================
        AuthenticationException customException = new AuthenticationException("Custom error message") {
            private static final long serialVersionUID = 1L;
        };

        // =================================================================
        // Act - Trigger unauthorized access
        // =================================================================
        authEntryPointJwt.commence(request, response, customException);

        // =================================================================
        // Assert - Verify error message in response
        // =================================================================
        String content = response.getContentAsString();
        assertTrue(content.contains("Custom error message"));
    }

    // =================================================================
    // Test different request paths
    // =================================================================
    @Test
    @DisplayName("Should handle different request paths")
    void commence_DifferentPaths() throws IOException, ServletException {
        // =================================================================
        // Arrange - Set different path
        // =================================================================
        request.setServletPath("/api/different/path");

        // =================================================================
        // Act - Trigger unauthorized access
        // =================================================================
        authEntryPointJwt.commence(request, response, authException);

        // =================================================================
        // Assert - Verify path in response
        // =================================================================
        String content = response.getContentAsString();
        assertTrue(content.contains("\"/api/different/path\""));
    }
}