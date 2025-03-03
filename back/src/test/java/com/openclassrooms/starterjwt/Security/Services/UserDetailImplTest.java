package com.openclassrooms.starterjwt.Security.Services;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;

// =================================================================
// Test class for UserDetailsImpl
// Tests user details creation, validation and comparison
// =================================================================

class UserDetailsImplTest {

    // =================================================================
    // Test basic UserDetailsImpl functionality and interface compliance
    // =================================================================

    @Test
    void testUserDetailsImpl() {
        // =================================================================
        // Arrange & Act - Create test user with builder pattern
        // =================================================================
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("testUser")
                .firstName("John")
                .lastName("Doe")
                .admin(false)
                .password("password123")
                .build();

        // =================================================================
        // Assert - Verify user details properties
        // =================================================================

        assertEquals(1L, userDetails.getId());
        assertEquals("testUser", userDetails.getUsername());
        assertEquals("John", userDetails.getFirstName());
        assertEquals("Doe", userDetails.getLastName());
        assertEquals(false, userDetails.getAdmin());
        assertEquals("password123", userDetails.getPassword());

        // =================================================================
        // Assert - Verify UserDetails interface implementation
        // =================================================================

        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());

        // =================================================================
        // Assert - Verify authorities
        // =================================================================

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertTrue(authorities.isEmpty());
    }

    // =================================================================
    // Test equals method implementation
    // =================================================================

    @Test
    void testEquals() {

        // =================================================================
        // Arrange - Create test users
        // =================================================================
        UserDetailsImpl user1 = UserDetailsImpl.builder().id(1L).username("user1").build();
        UserDetailsImpl user2 = UserDetailsImpl.builder().id(1L).username("user2").build();
        UserDetailsImpl user3 = UserDetailsImpl.builder().id(2L).username("user1").build();

        // =================================================================
        // Assert - Verify equals method
        // =================================================================

        assertTrue(user1.equals(user1)); // Same object
        assertTrue(user1.equals(user2)); // Same id
        assertFalse(user1.equals(user3)); // Different id
        assertFalse(user1.equals(null)); // Null comparison
        assertFalse(user1.equals(new Object())); // Different type
    }

    // =================================================================
    // Test builder pattern implementation
    // =================================================================

    @Test
    void testBuilder() {
        // =================================================================
        // Arrange & Act - Create user with all properties
        // =================================================================

        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("testUser")
                .firstName("John")
                .lastName("Doe")
                .admin(true)
                .password("password123")
                .build();

        // =================================================================
        // Assert - Verify all properties were set correctly
        // =================================================================
        assertNotNull(userDetails);
        assertEquals(1L, userDetails.getId());
        assertEquals("testUser", userDetails.getUsername());
        assertEquals("John", userDetails.getFirstName());
        assertEquals("Doe", userDetails.getLastName());
        assertTrue(userDetails.getAdmin());
        assertEquals("password123", userDetails.getPassword());
    }

}