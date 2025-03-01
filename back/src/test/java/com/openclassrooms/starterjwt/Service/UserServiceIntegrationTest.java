package com.openclassrooms.starterjwt.Service;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

// =================================================================
// Integration test class for UserService
// Uses full Spring context for testing
// =================================================================    
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("User Service Integration Tests")
public class UserServiceIntegrationTest {

    // =================================================================
    // Inject required services and repositories
    // =================================================================
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    // =================================================================
    // Define test data
    // =================================================================
    private User testUser;

    @BeforeEach
    void setUp() {

        // =================================================================
        // Clean database to ensure test isolation
        // =================================================================
        userRepository.deleteAll();

        // =================================================================
        // Create test user
        // =================================================================
        testUser = User.builder()
                .email("test@test.com")
                .firstName("Test")
                .lastName("User")
                .password("password")
                .admin(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // =================================================================
    // Test finding a user by ID
    // =================================================================

    @Test
    @DisplayName("Should find user by ID")
    void findById_Success() {
        // =================================================================
        // Arrange - Save test user to database
        // =================================================================
        User savedUser = userRepository.save(testUser);

        // =================================================================
        // Act - Retrieve user by ID
        // =================================================================
        User foundUser = userService.findById(savedUser.getId());

        // =================================================================
        // Assert - Verify that the found user is not null and matches the saved
        // user
        // =================================================================
        assertNotNull(foundUser);
        assertEquals(savedUser.getId(), foundUser.getId());
        assertEquals(savedUser.getEmail(), foundUser.getEmail());
        assertEquals(savedUser.getFirstName(), foundUser.getFirstName());
        assertEquals(savedUser.getLastName(), foundUser.getLastName());
    }

    // =================================================================
    // Test when user is not found
    // =================================================================
    @Test
    @DisplayName("Should return null when user not found")
    void findById_NotFound() {

        // =================================================================
        // Act - Attempt to find a non-existent user
        // =================================================================
        User foundUser = userService.findById(999L);

        // =================================================================
        // Assert - Verify that the found user is null
        // =================================================================
        assertNull(foundUser);
    }

    // =================================================================
    // Test deleting a user
    // =================================================================
    @Test
    @DisplayName("Should delete user successfully")
    void delete_Success() {

        // =================================================================
        // Arrange - Save test user to database
        // =================================================================
        User savedUser = userRepository.save(testUser);

        // =================================================================
        // Act - Delete the user
        // =================================================================
        userService.delete(savedUser.getId());

        // =================================================================
        // Assert - Verify that the user is deleted
        // =================================================================
        assertFalse(userRepository.findById(savedUser.getId()).isPresent());
    }

}