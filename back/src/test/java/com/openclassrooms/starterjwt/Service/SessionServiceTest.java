package com.openclassrooms.starterjwt.Service;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.services.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// =================================================================
// This is an integration test class for SessionService
// Uses @SpringBootTest to load the full application context
// =================================================================    

@SpringBootTest
@ActiveProfiles("test") // Use the test profile to load the test configuration
@DisplayName("Session Service Integration Tests")
public class SessionServiceTest {

    // =================================================================
    // Inject required services and repositories
    // =================================================================
    @Autowired
    private SessionService sessionService;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    // =================================================================
    // Define test data
    // =================================================================

    private Session testSession;
    private User testUser;
    private Teacher testTeacher;

    @BeforeEach
    void setUp() {
        // =================================================================
        // // Clean database to ensure test isolation
        // =================================================================
        sessionRepository.deleteAll();
        userRepository.deleteAll();
        teacherRepository.deleteAll();

        // =================================================================
        // Create test teacher
        // =================================================================
        testTeacher = Teacher.builder()
                .firstName("John")
                .lastName("Doe")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        testTeacher = teacherRepository.save(testTeacher);

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
        testUser = userRepository.save(testUser);

        // =================================================================
        // Create test session
        // =================================================================
        testSession = Session.builder()
                .name("Test Session")
                .date(new Date())
                .description("Test Description")
                .teacher(testTeacher)
                .users(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // =================================================================
    // Test the creation of a new session
    // =================================================================

    @Test
    @DisplayName("Should create session successfully")
    void createSession_Success() {

        // =================================================================
        // Act - Calls the service method to create a new session
        // =================================================================
        Session createdSession = sessionService.create(testSession);

        // =================================================================
        // Assert - Verify the session was created successfully
        // =================================================================

        assertNotNull(createdSession.getId());
        assertEquals(testSession.getName(), createdSession.getName());
        assertEquals(testSession.getDescription(), createdSession.getDescription());
    }

    // =================================================================
    // Test finding all sessions
    // =================================================================
    @Test
    @DisplayName("Should find all sessions")
    void findAllSessions_Success() {

        // =================================================================
        // Arrange - Saves a test session to the database
        // =================================================================
        sessionRepository.save(testSession);

        // =================================================================
        // Act - Retrieves all sessions
        // =================================================================
        List<Session> sessions = sessionService.findAll();

        // =================================================================
        // Assert - Verifies list contents
        // =================================================================
        assertFalse(sessions.isEmpty());
        assertEquals(1, sessions.size());
    }

    // =================================================================
    // Test finding a session by ID
    // =================================================================
    @Test
    @DisplayName("Should find session by ID")
    void findSessionById_Success() {

        // =================================================================
        // Arrange - Saves a test session and gets its ID
        // =================================================================
        Session savedSession = sessionRepository.save(testSession);

        // =================================================================
        // Act - Retrieves session by ID
        // =================================================================
        Session foundSession = sessionService.getById(savedSession.getId());

        // =================================================================
        // Assert - Verifies correct session retrieved
        // =================================================================
        assertNotNull(foundSession);
        assertEquals(savedSession.getId(), foundSession.getId());
    }

    // =================================================================
    // Test when session is not found
    // =================================================================
    @Test
    @DisplayName("Should return null when session not found")
    void findSessionById_NotFound() {

        // =================================================================
        // Act - Attempts to retrieve non-existent session
        // =================================================================
        Session foundSession = sessionService.getById(999L);

        // =================================================================
        // Assert - Verifies null return
        // =================================================================
        assertNull(foundSession);
    }

    // =================================================================
    // Test updating a session
    // =================================================================
    @Test
    @DisplayName("Should update session successfully")
    @Transactional
    void updateSession_Success() {

        // =================================================================
        // Arrange - Saves a test session and updates its name
        // =================================================================
        Session savedSession = sessionRepository.save(testSession);
        String updatedName = "Updated Session";
        savedSession.setName(updatedName);

        // =================================================================
        // Act - Updates the session
        // =================================================================
        Session updatedSession = sessionService.update(savedSession.getId(), savedSession);

        // =================================================================
        // Assert - Verifies update
        // =================================================================
        assertEquals(updatedName, updatedSession.getName());
    }

    // =================================================================
    // Test deleting a session
    // =================================================================
    @Test
    @DisplayName("Should delete session successfully")
    void deleteSession_Success() {

        // =================================================================
        // Arrange - Saves a test session
        // =================================================================
        Session savedSession = sessionRepository.save(testSession);

        // =================================================================
        // Act - Deletes the session
        // =================================================================
        sessionService.delete(savedSession.getId());

        // =================================================================
        // Assert - Verifies session was deleted
        // =================================================================

        assertFalse(sessionRepository.findById(savedSession.getId()).isPresent());
    }

    // =================================================================
    // Test adding a participant to a session
    // =================================================================
    @Test
    @DisplayName("Should add participant successfully")
    @Transactional
    void participate_Success() {
        // =================================================================
        // Arrange - Saves a test session
        // =================================================================
        Session savedSession = sessionRepository.save(testSession);

        // =================================================================
        // Act - Adds a participant to the session
        // =================================================================
        sessionService.participate(savedSession.getId(), testUser.getId());

        // =================================================================
        // Assert - Verifies participant was added
        // =================================================================
        Session updatedSession = sessionRepository.findById(savedSession.getId()).orElseThrow();
        assertTrue(updatedSession.getUsers().stream()
                .anyMatch(user -> user.getId().equals(testUser.getId())));
    }

    // =================================================================
    // Test when user already participates
    // =================================================================
    @Test
    @DisplayName("Should throw BadRequestException when user already participates")
    @Transactional
    void participate_UserAlreadyParticipates_ThrowsBadRequestException() {

        // =================================================================
        // Arrange - Creates session with user already participating
        // =================================================================
        testSession.getUsers().add(testUser);
        Session savedSession = sessionRepository.save(testSession);

        // =================================================================
        // Act & Assert - Attempts to add the same user again and verifies exception
        // =================================================================
        assertThrows(BadRequestException.class,
                () -> sessionService.participate(savedSession.getId(), testUser.getId()));
    }

    // =================================================================
    // Test when session is not found
    // =================================================================
    @Test
    @DisplayName("Should throw NotFoundException when session not found for participation")
    void participate_SessionNotFound_ThrowsNotFoundException() {

        // =================================================================
        // Act & Assert - Attempts to add a user to a non-existent session and verifies
        // exception
        // =================================================================
        assertThrows(NotFoundException.class, () -> sessionService.participate(999L,
                testUser.getId()));
    }

    // =================================================================
    // Test removing a participant from a session
    // =================================================================
    @Test
    @DisplayName("Should remove participant successfully")
    @Transactional
    void noLongerParticipate_Success() {

        // =================================================================
        // Arrange - Adds a test user to the session
        // =================================================================
        testSession.getUsers().add(testUser);
        Session savedSession = sessionRepository.save(testSession);

        // =================================================================
        // Act - Removes the user from the session
        // =================================================================
        sessionService.noLongerParticipate(savedSession.getId(),
                testUser.getId());

        // =================================================================
        // Assert - Verifies user was removed
        // =================================================================
        Session updatedSession = sessionRepository.findById(savedSession.getId()).orElseThrow();
        assertTrue(updatedSession.getUsers().isEmpty());
    }

    // =================================================================
    // Test when user is not a participant
    // =================================================================
    @Test
    @DisplayName("Should throw BadRequestException when user is not a participant")
    @Transactional
    void noLongerParticipate_UserNotParticipant_ThrowsBadRequestException() {

        // =================================================================
        // Arrange - Saves a test session
        // =================================================================
        Session savedSession = sessionRepository.save(testSession);

        // =================================================================
        // Act & Assert - Attempts to remove a user who is not a participant and
        // verifies exception
        // =================================================================
        assertThrows(BadRequestException.class,
                () -> sessionService.noLongerParticipate(savedSession.getId(),
                        testUser.getId()));
    }

    // =================================================================
    // Test when session is not found
    // =================================================================
    @Test
    @DisplayName("Should throw NotFoundException when session not found for removing participation")
    void noLongerParticipate_SessionNotFound_ThrowsNotFoundException() {

        // =================================================================
        // Act & Assert - Attempts to remove a user from a non-existent session and
        // verifies exception
        // =================================================================
        assertThrows(NotFoundException.class, () -> sessionService.noLongerParticipate(999L, testUser.getId()));
    }
}