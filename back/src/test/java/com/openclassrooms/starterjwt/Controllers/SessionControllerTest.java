package com.openclassrooms.starterjwt.Controllers;

import com.openclassrooms.starterjwt.controllers.SessionController;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

//===============================================================================================
// Unit tests for the SessionController WITH Mockito for mocking the dependencies 
//===============================================================================================  
@ExtendWith(MockitoExtension.class)
@DisplayName("SessionController Unit Tests")
public class SessionControllerTest {

    @Mock
    private SessionService sessionService;

    @Mock
    private SessionMapper sessionMapper;

    @InjectMocks
    private SessionController sessionController;

    // ==========================================
    // Test data
    // ==========================================
    private Session testSession;
    private SessionDto testSessionDto;
    private Teacher testTeacher;

    @BeforeEach
    void setUp() {

        // ==========================================
        // Setup test teacher
        // ==========================================
        testTeacher = Teacher.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // ==========================================
        // Setup test session
        // ==========================================

        testSession = Session.builder()
                .id(1L)
                .name("Test Session")
                .date(new Date())
                .description("Test Description")
                .teacher(testTeacher)
                .users(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // ==========================================
        // Setup test sessionDto
        // ==========================================
        testSessionDto = new SessionDto();
        testSessionDto.setId(1L);
        testSessionDto.setName("Test Session");
        testSessionDto.setDate(new Date());
        testSessionDto.setDescription("Test Description");
        testSessionDto.setTeacher_id(1L);
    }

    // =================================================================
    // Test to find session by valid ID
    // =================================================================
    @Test
    @DisplayName("Should find session by valid ID")
    void findById_ValidId_Success() {
        // ==========================================
        // Arrange: Mock the session service to return the test session
        // ==========================================
        when(sessionService.getById(1L)).thenReturn(testSession);
        when(sessionMapper.toDto(testSession)).thenReturn(testSessionDto);

        // ==========================================
        // Act: Call the findById method with a valid ID
        // ==========================================
        ResponseEntity<?> response = sessionController.findById("1");

        // ==========================================
        // Assert: Verify the response is a session entity
        // ==========================================
        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(sessionService).getById(1L);
        verify(sessionMapper).toDto(testSession);
    }

    // =================================================================
    // Test when the session is not found
    // =================================================================
    @Test
    @DisplayName("Should return not found for non-existent session")
    void findById_NonExistentId_NotFound() {
        // ==========================================
        // Arrange: Mock the session service to return null
        // ==========================================
        when(sessionService.getById(1L)).thenReturn(null);

        // ==========================================
        // Act: Call the findById method with a valid ID
        // ==========================================
        ResponseEntity<?> response = sessionController.findById("1");

        // ==========================================
        // Assert: Verify the response is not found
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
        // Act: Call the findById method with an invalid ID
        // ==========================================
        ResponseEntity<?> response = sessionController.findById("invalid");

        // ==========================================
        // Assert: Verify the response is bad request
        // ==========================================
        assertEquals(400, response.getStatusCodeValue());
    }

    // =================================================================
    // Test to find all sessions
    // =================================================================
    @Test
    @DisplayName("Should find all sessions")
    void findAll_Success() {
        // ==========================================
        // Arrange: Mock the session service
        // ==========================================
        List<Session> sessions = Arrays.asList(testSession);
        List<SessionDto> sessionDtos = Arrays.asList(testSessionDto);
        when(sessionService.findAll()).thenReturn(sessions);
        when(sessionMapper.toDto(sessions)).thenReturn(sessionDtos);

        // ==========================================
        // Act: Call the findAll method
        // ==========================================
        ResponseEntity<?> response = sessionController.findAll();

        // ==========================================
        // Assert: Verify the response
        // ==========================================
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(sessionDtos, response.getBody());
    }

    // =================================================================
    // Test session creation
    // =================================================================
    @Test
    @DisplayName("Should create new session successfully")
    void create_ValidSession_Success() {
        // ==========================================
        // Arrange: Mock the service and mapper
        // ==========================================
        when(sessionMapper.toEntity(testSessionDto)).thenReturn(testSession);
        when(sessionService.create(testSession)).thenReturn(testSession);
        when(sessionMapper.toDto(testSession)).thenReturn(testSessionDto);

        // ==========================================
        // Act: Call the create method
        // ==========================================
        ResponseEntity<?> response = sessionController.create(testSessionDto);

        // ==========================================
        // Assert: Verify the response
        // ==========================================
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(testSessionDto, response.getBody());
    }

    // =================================================================
    // Test session update
    // =================================================================
    @Test
    @DisplayName("Should update session successfully")
    void update_ValidSession_Success() {
        // ==========================================
        // Arrange: Mock the service and mapper
        // ==========================================
        when(sessionMapper.toEntity(testSessionDto)).thenReturn(testSession);
        when(sessionService.update(1L, testSession)).thenReturn(testSession);
        when(sessionMapper.toDto(testSession)).thenReturn(testSessionDto);

        // ==========================================
        // Act: Call the update method
        // ==========================================
        ResponseEntity<?> response = sessionController.update("1", testSessionDto);

        // ==========================================
        // Assert: Verify the response
        // ==========================================
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(testSessionDto, response.getBody());
    }

    @Test
    @DisplayName("Should return bad request when updating with invalid ID")
    void update_InvalidId_BadRequest() {
        // ==========================================
        // Act: Call the update method with invalid ID
        // ==========================================
        ResponseEntity<?> response = sessionController.update("invalid", testSessionDto);

        // ==========================================
        // Assert: Verify the response
        // ==========================================
        assertEquals(400, response.getStatusCodeValue());
    }

    // =================================================================
    // Test session deletion
    // =================================================================
    @Test
    @DisplayName("Should delete session successfully")
    void delete_ExistingSession_Success() {
        // ==========================================
        // Arrange: Mock the service
        // ==========================================
        when(sessionService.getById(1L)).thenReturn(testSession);
        doNothing().when(sessionService).delete(1L);

        // ==========================================
        // Act: Call the delete method
        // ==========================================
        ResponseEntity<?> response = sessionController.save("1");

        // ==========================================
        // Assert: Verify the response
        // ==========================================
        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(sessionService).delete(1L);
    }

    @Test
    @DisplayName("Should return not found when deleting non-existent session")
    void delete_NonExistentSession_NotFound() {
        // ==========================================
        // Arrange: Mock the service to return null
        // ==========================================
        when(sessionService.getById(1L)).thenReturn(null);

        // ==========================================
        // Act: Call the delete method
        // ==========================================
        ResponseEntity<?> response = sessionController.save("1");

        // ==========================================
        // Assert: Verify the response
        // ==========================================
        assertEquals(404, response.getStatusCodeValue());
        verify(sessionService, never()).delete(anyLong());
    }

    @Test
    @DisplayName("Should return bad request when deleting with invalid ID")
    void delete_InvalidId_BadRequest() {
        // ==========================================
        // Act: Call the delete method with invalid ID
        // ==========================================
        ResponseEntity<?> response = sessionController.save("invalid");

        // ==========================================
        // Assert: Verify the response
        // ==========================================
        assertEquals(400, response.getStatusCodeValue());
        verify(sessionService, never()).delete(anyLong());
    }

    // =================================================================
    // Test participation endpoints
    // =================================================================
    @Test
    @DisplayName("Should handle participation successfully")
    void participate_ValidIds_Success() {
        // ==========================================
        // Act: Call the participate method
        // ==========================================
        ResponseEntity<?> response = sessionController.participate("1", "1");

        // ==========================================
        // Assert: Verify the response
        // ==========================================
        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(sessionService).participate(1L, 1L);
    }

    @Test
    @DisplayName("Should return bad request when participating with invalid ID")
    void participate_InvalidId_BadRequest() {
        // ==========================================
        // Act: Call the participate method with invalid ID
        // ==========================================
        ResponseEntity<?> response = sessionController.participate("invalid", "1");

        // ==========================================
        // Assert: Verify the response
        // ==========================================
        assertEquals(400, response.getStatusCodeValue());
        verify(sessionService, never()).participate(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Should handle removing participation successfully")
    void noLongerParticipate_ValidIds_Success() {
        // ==========================================
        // Act: Call the noLongerParticipate method
        // ==========================================
        ResponseEntity<?> response = sessionController.noLongerParticipate("1", "1");

        // ==========================================
        // Assert: Verify the response
        // ==========================================
        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(sessionService).noLongerParticipate(1L, 1L);
    }

    @Test
    @DisplayName("Should return bad request when removing participation with invalid ID")
    void noLongerParticipate_InvalidId_BadRequest() {
        // ==========================================
        // Act: Call the noLongerParticipate method with invalid ID
        // ==========================================
        ResponseEntity<?> response = sessionController.noLongerParticipate("invalid", "1");

        // ==========================================
        // Assert: Verify the response
        // ==========================================
        assertEquals(400, response.getStatusCodeValue());
        verify(sessionService, never()).noLongerParticipate(anyLong(), anyLong());
    }
}