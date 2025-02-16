import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { TeacherService } from './teacher.service';
import { Teacher } from '../interfaces/teacher.interface';

describe('TeacherService', () => {
  let service: TeacherService;
  let httpTestingController: HttpTestingController;

  const mockTeachers: Teacher[] = [
    { id: 1, lastName: 'Toto', firstName: 'Titi', createdAt: new Date(), updatedAt: new Date() },
    { id: 2, lastName: 'Tata', firstName: 'Toto', createdAt: new Date(), updatedAt: new Date() }
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:[
        HttpClientTestingModule      // Use HttpClientTestingModule instead of HttpClientModule to simulate HTTP requests
      ], 
      providers: [TeacherService]
    });

    service = TestBed.inject(TeacherService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  //==================================================================================
  //==============================  Test to retrieve all teachers
  //==================================================================================

  it('should retrieve all teachers', (done) => {
    service.all().subscribe(teachers => {
      expect(teachers.length).toBe(2);           // Expect the number of teachers to be 2
      expect(teachers).toEqual(mockTeachers);    // Expect the teachers to be equal to mockTeachers
      done();
    });

    const req = httpTestingController.expectOne('api/teacher'); // Setup an HTTP GET request to 'api/teacher'
    expect(req.request.method).toBe('GET');                     // Expect a GET request to 'api/teacher'
    req.flush(mockTeachers);                                    // simulating a successful server response with mockTeachers 
    httpTestingController.verify();                             // Verify that the HTTP request was made
  });

  //==================================================================================
  //==============================  Test to retrieve details of a single teacher
  //==================================================================================

  it('should retrieve details of a single teacher by ID', (done) => {

    const expectedTeacher = mockTeachers.find(t => t.id === 1);     // Find the teacher with id 1

    if (!expectedTeacher) {
      throw new Error('Expected teacher not found in mock data');   // If the teacher is not found, throw an error
    }

    service.detail('1').subscribe(teacher => {
      expect(teacher.id).toBe(1);                              // Expect the teacher id to be 1
      expect(teacher).toEqual(expectedTeacher);                // Expect the teacher to be equal to expectedTeacher 
      done();
    });

    const req = httpTestingController.expectOne('api/teacher/1'); // Setup an HTTP GET request to 'api/teacher/1'
    expect(req.request.method).toBe('GET');                       // Expect a GET request to 'api/teacher/1'
    req.flush(expectedTeacher);                                   // simulating a successful server response with expectedTeacher 
    httpTestingController.verify();                               // Verify that the HTTP request was made
  });

});
