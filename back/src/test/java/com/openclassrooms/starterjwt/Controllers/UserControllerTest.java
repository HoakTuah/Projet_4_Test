package com.openclassrooms.starterjwt.Controllers;

import com.openclassrooms.starterjwt.controllers.UserController;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

//===============================================================================================
// Unit tests for the UserController WITH Mockito for mocking the dependencies 
//===============================================================================================  

@ExtendWith(MockitoExtension.class) // Mockito extension to use Mockito annotations
@DisplayName("UserController Unit Tests")
public class UserControllerTest {

    @Mock
    private UserService userService;// Mock object for user service

    @Mock
    private UserMapper userMapper; // Mock object for user mapper

    @Mock
    private SecurityContext securityContext; // Mock object for security context

    @Mock
    private Authentication authentication; // Mock object for authentication

    @Mock
    private UserDetails userDetails; // Mock object for user details

    @InjectMocks
    private UserController userController; // Injects mock objects into the controller

    // ==========================================
    // Test data
    // ==========================================

    private User testUser;

    @BeforeEach

    void setUp() {

        // ==========================================
        // Setup test user
        // ==========================================
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@test.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setPassword("password");

        // ==========================================
        // Setup Security Context Mock
        // ==========================================
        SecurityContextHolder.setContext(securityContext);
    }

    // =================================================================
    // Test to find user by valid ID
    // =================================================================
    @Test
    @DisplayName("Should find user by valid ID")
    void findById_ValidId_Success() {

        // ==========================================
        // Arrange: Mock the user service to return the test user
        // ==========================================
        when(userService.findById(1L)).thenReturn(testUser);
        when(userMapper.toDto(testUser)).thenReturn(null); // Replace null with actual DTO if needed

        // ==========================================
        // Act: Call the findById method with a valid ID
        // ==========================================
        ResponseEntity<?> response = userController.findById("1");

        // ==========================================
        // Assert: Verify the response and service calls
        // ==========================================
        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(userService).findById(1L);
        verify(userMapper).toDto(testUser);

    }

    // =================================================================
    // Test when the user is not found
    // =================================================================
    @Test
    @DisplayName("Should return not found for non-existent user")
    void findById_NonExistentId_NotFound() {
        // ==========================================
        // Arrange: Mock the user service to return null
        // ==========================================
        when(userService.findById(1L)).thenReturn(null);

        // ==========================================
        // Act: Call the findById method with a valid ID
        // ==========================================
        ResponseEntity<?> response = userController.findById("1");

        // ==========================================
        // Assert: Verify 404 response
        // ==========================================
        assertEquals(404, response.getStatusCodeValue());
    }

    // =================================================================
    // Test when the ID format is invalid
    // =================================================================
    @Test
    @DisplayName("Should return bad request for invalid ID format")
    void findById_InvalidId_BadRequest() {

        // ==========================================
        // Act: Call the findById method with a valid ID
        // ==========================================
        ResponseEntity<?> response = userController.findById("invalid");

        // ==========================================
        // Assert: Verify 400 bad request response
        // ==========================================
        assertEquals(400, response.getStatusCodeValue());
    }

    // =================================================================
    // Test authorized user deletion
    // =================================================================

    @Test
    @DisplayName("Should delete user when authorized")
    void delete_AuthorizedUser_Success() {

        // ==========================================
        // Arrange: Setup security context for authorized user
        // ==========================================
        when(userService.findById(1L)).thenReturn(testUser);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("test@test.com");

        // ==========================================
        // Act: Attempt to delete the test user
        // ==========================================
        ResponseEntity<?> response = userController.save("1");

        // ==========================================
        // Assert: Verify successful deletion
        // ==========================================
        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(userService).delete(1L);
    }

    // =================================================================
    // Test unauthorized user deletion attempt
    // =================================================================

    @Test
    @DisplayName("Should return unauthorized when deleting other user's account")
    void delete_UnauthorizedUser_Unauthorized() {

        // ==========================================
        // Arrange: Mock the user service to return the test user
        // ==========================================
        when(userService.findById(1L)).thenReturn(testUser);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("other@test.com");

        // ==========================================
        // Act: Attempt to delete the test user
        // ==========================================
        ResponseEntity<?> response = userController.save("1");

        // ==========================================
        // Assert: Verify unauthorized response
        // ==========================================
        assertEquals(401, response.getStatusCodeValue());
        verify(userService, never()).delete(anyLong());
    }

    // =================================================================
    // Test deletion of non-existent user
    // =================================================================

    @Test
    @DisplayName("Should return not found when deleting non-existent user")
    void delete_NonExistentUser_NotFound() {

        // ==========================================
        // Arrange: Mock service to return null for non-existent user
        // ==========================================
        when(userService.findById(1L)).thenReturn(null);

        // ==========================================
        // Act: Attempt to delete non-existent user
        // ==========================================
        ResponseEntity<?> response = userController.save("1");

        // ==========================================
        // Assert: Verify not found response
        // ==========================================
        assertEquals(404, response.getStatusCodeValue());
        verify(userService, never()).delete(anyLong());
    }

    // =================================================================
    // Test invalid ID format for deletion
    // =================================================================

    @Test
    @DisplayName("Should return bad request when deleting with invalid ID format")
    void delete_InvalidId_BadRequest() {
        // ==========================================
        // Act: Attempt deletion with invalid ID format
        // ==========================================
        ResponseEntity<?> response = userController.save("invalid");

        // ==========================================
        // Assert: Verify bad request response
        // ==========================================
        assertEquals(400, response.getStatusCodeValue());
        verify(userService, never()).delete(anyLong());
    }
}