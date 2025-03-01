package com.openclassrooms.starterjwt.Controllers;

import com.openclassrooms.starterjwt.controllers.TeacherController;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.TeacherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

//===============================================================================================
// Unit tests for the TeacherController WITH Mockito for mocking the dependencies 
//===============================================================================================      

@ExtendWith(MockitoExtension.class) // Mockito extension to use Mockito annotations

@DisplayName("TeacherController Unit Tests")
class TeacherControllerTest {

    @Mock
    private TeacherService teacherService; // Mock object for teacher service
    @Mock
    private TeacherMapper teacherMapper; // Mock object for teacher mapper

    @InjectMocks
    private TeacherController teacherController; // Injects mock objects into the controller

    // ==========================================
    // Test data
    // ==========================================

    private Teacher teacher;

    @BeforeEach

    void setUp() {

        // ==========================================
        // Setup teacher entity
        // ==========================================
        teacher = new Teacher();
        teacher.setId(1L);
        teacher.setLastName("Doe");
        teacher.setFirstName("John");
        teacher.setCreatedAt(LocalDateTime.now());
        teacher.setUpdatedAt(LocalDateTime.now());

    }

    // =================================================================
    // Test to find teacher by valid ID
    // =================================================================

    @Test
    @DisplayName("Should find teacher by valid ID")
    void findById_WithValidId_ReturnsTeacher() {

        // ==========================================
        // Arrange: Mock the teacher service to return the teacher entity
        // ==========================================

        when(teacherService.findById(1L)).thenReturn(teacher);

        // ==========================================
        // Act: Call the findById method with a valid ID
        // ==========================================

        ResponseEntity<?> response = teacherController.findById("1");

        // ==========================================
        // Assert: Verify the response is a teacher entity
        // ==========================================

        assertEquals(HttpStatus.OK, response.getStatusCode()); // Verify HTTP 200 OK status
    }

    // =================================================================
    // Test when the teacher is not found
    // =================================================================

    @Test
    @DisplayName("Should return not found for non-existent teacher ID")
    void findById_WithNonExistentId_ReturnsNotFound() {

        // ==========================================
        // Arrange: Mock the teacher service to return null
        // ==========================================

        when(teacherService.findById(1L)).thenReturn(null);

        // ==========================================
        // Act: Call the findById method with a non-existent ID
        // ==========================================

        ResponseEntity<?> response = teacherController.findById("1");

        // ==========================================
        // Assert: Verify the response is a not found status
        // ==========================================

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode()); // Verify HTTP 404 Not Found status
    }

    // =================================================================
    // Test when the ID is invalid
    // =================================================================

    @Test
    @DisplayName("Should return bad request for invalid ID format")
    void findById_WithInvalidId_ReturnsBadRequest() {

        // ==========================================
        // Act: Call the findById method with an invalid ID
        // ==========================================

        ResponseEntity<?> response = teacherController.findById("invalid");

        // ==========================================
        // Assert: Verify the response is a bad request status
        // ==========================================

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()); // Verify HTTP 400 Bad Request status
    }

    // =================================================================
    // Test to find all teachers
    // =================================================================

    @Test
    @DisplayName("Should find all teachers")
    void findAll_ReturnsAllTeachers() {

        // ==========================================
        // Arrange: Mock the teacher service to return the teacher entity
        // ==========================================

        List<Teacher> teachers = Arrays.asList(teacher); // Create list with our test teacher
        when(teacherService.findAll()).thenReturn(teachers); // Mock service to return teacher list

        // ==========================================
        // Act: Call the findAll method
        // ==========================================

        ResponseEntity<?> response = teacherController.findAll();

        // ==========================================
        // Assert: Verify the response is a ok status
        // ==========================================
        assertEquals(HttpStatus.OK, response.getStatusCode()); // Verify HTTP 200 OK status
    }
}