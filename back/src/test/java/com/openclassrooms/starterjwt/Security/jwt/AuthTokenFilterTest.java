package com.openclassrooms.starterjwt.Security.jwt;

import com.openclassrooms.starterjwt.security.jwt.AuthTokenFilter;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.context.SecurityContextHolder;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;

import org.junit.jupiter.api.DisplayName;
// =================================================================
// Integration test class for AuthTokenFilter
// Tests JWT token validation and authentication process
// =================================================================

@ExtendWith(MockitoExtension.class)
class AuthTokenFilterTest {

    // =================================================================
    // Inject required components and mocks
    // =================================================================

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private AuthTokenFilter authTokenFilter;

    private UserDetails userDetails;

    // =================================================================
    // Initialize test objects
    // =================================================================

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@test.com")
                .firstName("Test")
                .lastName("User")
                .password("password")
                .build();
    }

    // =================================================================
    // Test successful authentication with valid token
    // =================================================================

    @Test
    @DisplayName("Should authenticate user with valid token")
    void doFilter_WithValidToken_ShouldAuthenticate() throws ServletException, IOException {

        // =================================================================
        // Arrange - Set up valid token and authentication
        // =================================================================

        String token = "valid.jwt.token";
        String bearerToken = "Bearer " + token;

        when(request.getHeader("Authorization")).thenReturn(bearerToken);
        when(jwtUtils.validateJwtToken(token)).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(token)).thenReturn("test@test.com");
        when(userDetailsService.loadUserByUsername("test@test.com")).thenReturn(userDetails);

        // =================================================================
        // Act - Process the filter chain
        // =================================================================
        authTokenFilter.doFilter(request, response, filterChain);

        // =================================================================
        // Assert - Verify authentication process
        // =================================================================
        verify(filterChain).doFilter(request, response);
        verify(userDetailsService).loadUserByUsername("test@test.com");
        verify(jwtUtils).validateJwtToken(token);
        verify(jwtUtils).getUserNameFromJwtToken(token);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    // =================================================================
    // Test authentication failure with invalid token
    // =================================================================

    @Test
    @DisplayName("Should not authenticate user with invalid token")
    void doFilter_WithInvalidToken_ShouldNotAuthenticate() throws ServletException, IOException {

        // =================================================================
        // Arrange - Setup invalid token scenario
        // =================================================================

        String token = "invalid.jwt.token";
        String bearerToken = "Bearer " + token;

        when(request.getHeader("Authorization")).thenReturn(bearerToken);
        when(jwtUtils.validateJwtToken(token)).thenReturn(false);

        // =================================================================
        // Act - Process the filter chain
        // =================================================================
        authTokenFilter.doFilter(request, response, filterChain);

        // =================================================================
        // Assert - Verify authentication failure
        // =================================================================

        verify(filterChain).doFilter(request, response);
        verify(jwtUtils).validateJwtToken(token);
        verify(jwtUtils, never()).getUserNameFromJwtToken(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    // =================================================================
    // Test authentication attempt without token
    // =================================================================

    @Test
    @DisplayName("Should not authenticate user with no token")
    void doFilter_WithNoToken_ShouldNotAuthenticate() throws ServletException, IOException {

        // =================================================================
        // Arrange - Setup scenario with no token
        // =================================================================
        when(request.getHeader("Authorization")).thenReturn(null);

        // =================================================================
        // Act - Process the filter chain
        // =================================================================
        authTokenFilter.doFilter(request, response, filterChain);

        // =================================================================
        // Assert - Verify no authentication attempt
        // =================================================================
        verify(filterChain).doFilter(request, response);
        verify(jwtUtils, never()).validateJwtToken(anyString());
        verify(jwtUtils, never()).getUserNameFromJwtToken(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

}