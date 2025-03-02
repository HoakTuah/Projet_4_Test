package com.openclassrooms.starterjwt.Payload;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import com.openclassrooms.starterjwt.payload.response.JwtResponse;
import com.openclassrooms.starterjwt.payload.response.MessageResponse;

// =================================================================
// Verify the functionality of data transfer objects used in API responses
// Testing both MessageResponse and JwtResponse classes
// =================================================================

class ResponseTest {

    // =================================================================
    // Tests for MessageResponse class
    // =================================================================
    @Nested
    class MessageResponseTest {

        // =================================================================
        // Test constructor and getter functionality
        // 1. Constructor properly stores the message
        // 2. Getter returns the stored message
        // =================================================================
        @Test
        @DisplayName("Should correctly store and retrieve message")
        void testConstructorAndGetter() {
            // =================================================================
            // Arrange - Create test message
            // =================================================================
            String testMessage = "Test message";

            // =================================================================
            // Act - Create response object
            // =================================================================
            MessageResponse response = new MessageResponse(testMessage);

            // =================================================================
            // Assert - Verify message is correctly stored
            // =================================================================
            assertEquals(testMessage, response.getMessage());
        }

        // =================================================================
        // Test setter functionality
        // 1. Message can be updated after creation
        // 2. Updated message is correctly stored
        // =================================================================

        @Test
        @DisplayName("Should update and retrieve new message")
        void testSetter() {
            // =================================================================
            // Arrange - Create response and new message
            // =================================================================
            MessageResponse response = new MessageResponse("Initial message");
            String newMessage = "Updated message";

            // =================================================================
            // Act - Update message
            // =================================================================
            response.setMessage(newMessage);

            // =================================================================
            // Assert - Verify message was updated
            // =================================================================
            assertEquals(newMessage, response.getMessage());
        }

        // =================================================================
        // Test null handling in setter
        // 1. Existing message can be set to null
        // 2. Null is properly stored after update
        // =================================================================
        @Test
        @DisplayName("Should handle setting message to null")
        void testSetterWithNullMessage() {

            // =================================================================
            // Arrange - Create response with initial message
            // =================================================================
            MessageResponse response = new MessageResponse("Initial message");

            // =================================================================
            // Act - Set message to null
            // =================================================================
            response.setMessage(null);

            // =================================================================
            // Assert - Verify null was properly stored
            // =================================================================
            assertNull(response.getMessage());
        }
    }

    // =================================================================
    // Tests for JwtResponse class
    // =================================================================
    @Nested
    class JwtResponseTest {

        // =================================================================
        // Test constructor and getters functionality
        // 1. All fields are properly initialized
        // 2. Default values are set correctly
        // 3. All getters return correct values
        // =================================================================
        @Test
        @DisplayName("Should properly initialize and retrieve all fields")
        void testConstructorAndGetters() {
            // =================================================================
            // Arrange - Define test values
            // =================================================================
            String token = "test.jwt.token";
            Long id = 1L;
            String username = "test@test.com";
            String firstName = "John";
            String lastName = "Doe";
            Boolean admin = true;

            // =================================================================
            // Act - Create response object
            // =================================================================

            JwtResponse response = new JwtResponse(token, id, username, firstName, lastName, admin);

            // =================================================================
            // Assert - Verify all fields are correctly stored
            // =================================================================
            assertEquals(token, response.getToken());
            assertEquals("Bearer", response.getType());
            assertEquals(id, response.getId());
            assertEquals(username, response.getUsername());
            assertEquals(firstName, response.getFirstName());
            assertEquals(lastName, response.getLastName());
            assertEquals(admin, response.getAdmin());
        }

        // =================================================================
        // Test setters functionality
        // 1. All fields can be updated
        // 2. Updates are properly stored
        // 3. All fields are independent
        // =================================================================
        @Test
        @DisplayName("Should properly update all fields")
        void testSetters() {

            // =================================================================
            // Arrange - Create response and new values
            // =================================================================
            JwtResponse response = new JwtResponse("initial.token", 1L, "initial@test.com", "John", "Doe", false);

            String newToken = "new.jwt.token";
            String newType = "NewType";
            Long newId = 2L;
            String newUsername = "new@test.com";
            String newFirstName = "Jane";
            String newLastName = "Smith";
            Boolean newAdmin = true;

            // =================================================================
            // Act - Update all fields
            // =================================================================
            response.setToken(newToken);
            response.setType(newType);
            response.setId(newId);
            response.setUsername(newUsername);
            response.setFirstName(newFirstName);
            response.setLastName(newLastName);
            response.setAdmin(newAdmin);

            // =================================================================
            // Assert - Verify all fields were updated
            // =================================================================
            assertEquals(newToken, response.getToken());
            assertEquals(newType, response.getType());
            assertEquals(newId, response.getId());
            assertEquals(newUsername, response.getUsername());
            assertEquals(newFirstName, response.getFirstName());
            assertEquals(newLastName, response.getLastName());
            assertEquals(newAdmin, response.getAdmin());
        }

        // =================================================================
        // Test null handling in constructor
        // 1. Constructor accepts null for all fields
        // 2. Default values are maintained
        // 3. Null values are properly stored
        // =================================================================
        @Test
        @DisplayName("Should handle null values in constructor")
        void testConstructorWithNullValues() {

            // =================================================================
            // Arrange & Act - Create response with all null values
            // =================================================================

            JwtResponse response = new JwtResponse(null, null, null, null, null, null);

            // =================================================================
            // Assert - Verify null handling and default values
            // =================================================================
            assertNull(response.getToken());
            assertEquals("Bearer", response.getType());
            assertNull(response.getId());
            assertNull(response.getUsername());
            assertNull(response.getFirstName());
            assertNull(response.getLastName());
            assertNull(response.getAdmin());
        }
    }
}