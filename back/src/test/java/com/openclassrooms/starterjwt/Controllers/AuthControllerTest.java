package com.openclassrooms.starterjwt.Controllers;

import com.openclassrooms.starterjwt.controllers.AuthController;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.payload.response.JwtResponse;
import com.openclassrooms.starterjwt.payload.response.MessageResponse;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

//===============================================================================================
// Unit tests for the AuthController WITH Mockito for mocking the dependencies 
//===============================================================================================   

@ExtendWith(MockitoExtension.class) // Mockito extension to use Mockito annotations

@DisplayName("AuthController Unit Tests")
public class AuthControllerTest {

        @Mock
        private AuthenticationManager authenticationManager; // Mock object for authentication management
        @Mock
        private UserRepository userRepository; // Mock object for user data access
        @Mock
        private PasswordEncoder passwordEncoder; // Mock object for password encryption
        @Mock
        private JwtUtils jwtUtils; // Mock object for JWT token operations
        @Mock
        private Authentication authentication; // Mock object for authentication process

        @InjectMocks
        private AuthController authController; // Injects mock objects into the controller

        // ==========================================
        // Test data
        // ==========================================

        private LoginRequest loginRequest; // Object to hold login credentials
        private SignupRequest signupRequest; // Object to hold registration data
        private User user; // Object to represent a user entity
        private UserDetailsImpl userDetails; // Object to hold user details for security

        @BeforeEach
        void setUp() {

                // ==========================================
                // Setup login request
                // ==========================================
                loginRequest = new LoginRequest();
                loginRequest.setEmail("test@test.com");
                loginRequest.setPassword("password123");

                // ==========================================
                // Setup signup request
                // ==========================================
                signupRequest = new SignupRequest();
                signupRequest.setEmail("test@test.com");
                signupRequest.setFirstName("John");
                signupRequest.setLastName("Doe");
                signupRequest.setPassword("password123");

                // ==========================================
                // Setup user
                // ==========================================
                user = new User();
                user.setId(1L);
                user.setEmail("test@test.com");
                user.setFirstName("John");
                user.setLastName("Doe");
                user.setPassword("encodedPassword");
                user.setAdmin(false);

                // ==========================================
                // Setup UserDetails
                // ==========================================
                userDetails = UserDetailsImpl.builder()
                                .id(user.getId())
                                .username(user.getEmail())
                                .firstName(user.getFirstName())
                                .lastName(user.getLastName())
                                .password(user.getPassword())
                                .admin(user.isAdmin())
                                .build();
        }

        // ==========================================
        // Test to authenticate a regular user
        // ==========================================

        @Test
        @DisplayName("Should successfully authenticate a regular user")
        void authenticateUser_Success() {

                // ==========================================
                // Arrange: Mock authentication process
                // ==========================================

                when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                                .thenReturn(authentication); // Mock the authentication process (returns a valid
                                                             // authentication token)
                when(authentication.getPrincipal())
                                .thenReturn(userDetails); // Mock the authentication principal (returns the user
                                                          // details)
                when(jwtUtils.generateJwtToken(authentication)) // Mock the JWT token generation
                                .thenReturn("fake-jwt-token"); // (returns a fake JWT token)
                when(userRepository.findByEmail(userDetails // Mock the user details based on the email
                                .getUsername())).thenReturn(Optional.of(user)); // (returns the user details )

                // ==========================================
                // Act: Perform the authentication
                // ==========================================

                ResponseEntity<?> response = authController.authenticateUser(loginRequest);

                // ==========================================
                // Assert: Verify the response is a JwtResponse and user is not an admin
                // ==========================================
                assertTrue(response.getBody() instanceof JwtResponse); // Check if the response is a JwtResponse
                JwtResponse jwtResponse = (JwtResponse) response.getBody(); // Cast the response body to a JwtResponse
                assertEquals("fake-jwt-token", jwtResponse.getToken()); // Verify the token is correct
                assertEquals(userDetails.getUsername(), jwtResponse.getUsername()); // Verify the username is correct
                assertEquals(userDetails.getFirstName(), jwtResponse.getFirstName()); // Verify the first name is
                                                                                      // correct
                assertEquals(userDetails.getLastName(), jwtResponse.getLastName()); // Verify the last name is correct
                assertFalse(jwtResponse.getAdmin()); // Verify the user is not an admin
        }

        // ==========================================
        // Test to authenticate an admin user
        // ==========================================

        @Test
        @DisplayName("Should successfully authenticate an admin user")
        void authenticateUser_AdminUser() {

                // ==========================================
                // Arrange: Mock authentication process and provide admin user details
                // ==========================================
                user.setAdmin(true);
                userDetails = UserDetailsImpl.builder()
                                .id(user.getId())
                                .username(user.getEmail())
                                .firstName(user.getFirstName())
                                .lastName(user.getLastName())
                                .password(user.getPassword())
                                .admin(user.isAdmin())
                                .build();

                // ==========================================
                // Same process of regular user authentication
                // ==========================================
                when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                                .thenReturn(authentication); // Mock the authentication process (returns a valid
                                                             // authentication token)
                when(authentication.getPrincipal()).thenReturn(userDetails);
                when(jwtUtils.generateJwtToken(authentication)).thenReturn("fake-jwt-token");
                when(userRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(user));

                // ==========================================
                // Act: Perform the authentication
                // ==========================================
                ResponseEntity<?> response = authController.authenticateUser(loginRequest);

                // ==========================================
                // Assert: Verify the response is a JwtResponse and user is an admin
                // ==========================================
                assertTrue(response.getBody() instanceof JwtResponse);
                JwtResponse jwtResponse = (JwtResponse) response.getBody();
                assertTrue(jwtResponse.getAdmin());
        }

        // ==========================================
        // Test to register a new user successfully
        // ==========================================

        @Test
        @DisplayName("Should successfully register a new user")
        void registerUser_Success() {

                // ==========================================
                // Arrange: Mock user registration process
                // ==========================================

                // Mock email and return that email is not taken
                when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(false);
                // Mock password encoding (returns encoded version of password)
                when(passwordEncoder.encode(signupRequest.getPassword())).thenReturn("encodedPassword");

                // ==========================================
                // Act: Perform the registration
                // ==========================================
                ResponseEntity<?> response = authController.registerUser(signupRequest);

                // ==========================================
                // Assert: Verify successful registration
                // ==========================================
                assertTrue(response.getBody() instanceof MessageResponse); // Check if the response is a MessageResponse
                MessageResponse messageResponse = (MessageResponse) response.getBody(); // Cast response to
                                                                                        // MessageResponse
                assertEquals("User registered successfully!", messageResponse.getMessage()); // Verify if the message is
                                                                                             // correct
                verify(userRepository).save(any(User.class)); // Verify the user repository save method is called
        }

        // ==========================================
        // Test to register a new user with an already taken email
        // ==========================================

        @Test
        @DisplayName("Should return error when email is already taken")
        void registerUser_EmailTaken() {

                // ==========================================
                // Arrange: Mock user registration process
                // ==========================================
                when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(true);

                // ==========================================
                // Act: Perform the registration
                // ==========================================
                ResponseEntity<?> response = authController.registerUser(signupRequest);

                // ==========================================
                // Assert: Verify error when email is already taken
                // ==========================================
                assertTrue(response.getBody() instanceof MessageResponse); // Check if the response is a MessageResponse
                MessageResponse messageResponse = (MessageResponse) response.getBody(); // Cast response to
                                                                                        // MessageResponse
                assertEquals("Error: Email is already taken!", messageResponse.getMessage()); // Verify error message
                verify(userRepository, never()).save(any(User.class)); // Verify save was never called
        }

}