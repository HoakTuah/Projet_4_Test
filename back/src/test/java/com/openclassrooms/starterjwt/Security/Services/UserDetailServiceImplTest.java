package com.openclassrooms.starterjwt.Security.Services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;

@ExtendWith(MockitoExtension.class)

// =================================================================
// Test class for UserDetailsServiceImpl
// Tests user details service functionality and error handling
// =================================================================

class UserDetailsServiceImplTest {

    // =================================================================
    // Inject required components and mocks
    // =================================================================

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User testUser;
    private final String TEST_EMAIL = "test@test.com";

    // =================================================================
    // Initialize test environment before each test
    // =================================================================

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail(TEST_EMAIL);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setPassword("password123");
    }

    // =================================================================
    // Test successful user loading scenario
    // =================================================================

    @Test
    void loadUserByUsername_ValidEmail_ReturnsUserDetails() {

        // =================================================================
        // Arrange - Setup mock repository response
        // =================================================================

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));

        // =================================================================
        // Act - Load user by email
        // =================================================================

        UserDetails userDetails = userDetailsService.loadUserByUsername(TEST_EMAIL);

        // =================================================================
        // Assert - Verify returned user details
        // =================================================================

        assertNotNull(userDetails);
        assertTrue(userDetails instanceof UserDetailsImpl);
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetails;

        assertEquals(testUser.getId(), userDetailsImpl.getId());
        assertEquals(testUser.getEmail(), userDetailsImpl.getUsername());
        assertEquals(testUser.getFirstName(), userDetailsImpl.getFirstName());
        assertEquals(testUser.getLastName(), userDetailsImpl.getLastName());
        assertEquals(testUser.getPassword(), userDetailsImpl.getPassword());

        verify(userRepository, times(1)).findByEmail(TEST_EMAIL);
    }

    // =================================================================
    // Test invalid email scenario
    // =================================================================

    @Test
    void loadUserByUsername_InvalidEmail_ThrowsException() {

        // =================================================================
        // Arrange - Setup mock for non-existent email
        // =================================================================

        String nonExistentEmail = "nonexistent@test.com";
        when(userRepository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

        // =================================================================
        // Act & Assert - Verify exception throwing
        // =================================================================

        Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(nonExistentEmail);
        });

        String expectedMessage = "User Not Found with email: " + nonExistentEmail;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(userRepository, times(1)).findByEmail(nonExistentEmail);
    }

    // =================================================================
    // Test null email scenario
    // =================================================================

    @Test
    void loadUserByUsername_NullEmail_ThrowsException() {
        // =================================================================
        // Act & Assert - Verify null handling
        // =================================================================
        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(null);
        });
    }
}