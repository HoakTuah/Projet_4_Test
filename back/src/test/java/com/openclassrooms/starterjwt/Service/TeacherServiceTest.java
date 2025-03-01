package com.openclassrooms.starterjwt.Service;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.services.TeacherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// =================================================================
// This is an integration test class for TeacherService
// Uses @SpringBootTest to load the full application context
// =================================================================  

@SpringBootTest
@ActiveProfiles("test") // Use the test profile to load the test configuration
@DisplayName("Teacher Service Integration Tests")
public class TeacherServiceTest {

    // =================================================================
    // Inject required services and repositories
    // =================================================================
    @Autowired
    private TeacherService teacherService;

    @Autowired
    private TeacherRepository teacherRepository;

    // =================================================================
    // Define test data
    // =================================================================
    private Teacher testTeacher;

    @BeforeEach
    void setUp() {

        // =================================================================
        // // Clean database to ensure test isolation
        // =================================================================
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
    }

    // =================================================================
    // Test finding all teachers
    // =================================================================

    @Test
    @DisplayName("Should find all teachers")
    void findAll_Success() {

        // =================================================================
        // Arrange - Save two teachers to database
        // =================================================================
        teacherRepository.save(testTeacher);
        Teacher secondTeacher = Teacher.builder()
                .firstName("Jane")
                .lastName("Smith")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        teacherRepository.save(secondTeacher);

        // =================================================================
        // Act - Retrieve all teachers
        // =================================================================
        List<Teacher> teachers = teacherService.findAll();

        // =================================================================
        // Assert - Verify that the list is not empty and contains two teachers
        // =================================================================
        assertFalse(teachers.isEmpty());
        assertEquals(2, teachers.size());
    }

    // =================================================================
    // Test finding a teacher by ID
    // =================================================================
    @Test
    @DisplayName("Should find teacher by ID")
    void findById_Success() {

        // =================================================================
        // Arrange - Save test teacher to database
        // =================================================================
        Teacher savedTeacher = teacherRepository.save(testTeacher);

        // =================================================================
        // Act - Retrieve teacher by ID
        // =================================================================
        Teacher foundTeacher = teacherService.findById(savedTeacher.getId());

        // =================================================================
        // Assert - Verify that the found teacher is not null and matches the saved
        // teacher
        // =================================================================
        assertNotNull(foundTeacher);
        assertEquals(savedTeacher.getId(), foundTeacher.getId());
        assertEquals(savedTeacher.getFirstName(), foundTeacher.getFirstName());
        assertEquals(savedTeacher.getLastName(), foundTeacher.getLastName());
    }

    // =================================================================
    // Test when teacher is not found
    // =================================================================
    @Test
    @DisplayName("Should return null when teacher not found")
    void findById_NotFound() {

        // =================================================================
        // Act - Attempt to find a non-existent teacher
        // =================================================================
        Teacher foundTeacher = teacherService.findById(999L);

        // =================================================================
        // Assert - Verify that the found teacher is null
        // =================================================================
        assertNull(foundTeacher);
    }
}