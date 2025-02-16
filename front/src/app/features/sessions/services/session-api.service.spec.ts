import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import { Session } from '../interfaces/session.interface';
import { SessionApiService } from './session-api.service';

describe('SessionsService', () => {
  let service: SessionApiService;
  let httpTestingController: HttpTestingController;

  // Mock data for sessions
  const mockSessions: Session[] = [
    { 
      id: 1,
      name: 'Yoga Session',
      description: 'Beginner yoga class',
      date: new Date(),
      teacher_id: 1,
      users: [1, 2]
    },

    { 
      id: 2,
      name: 'Pilates Session',
      description: 'Advanced pilates',
      date: new Date(),
      teacher_id: 2,
      users: [3, 4]
    }
  ];

  const newSession: Session = {
    id: 3,
    name: 'New Yoga Session',
    description: 'New session description',
    date: new Date(),
    teacher_id: 1,
    users: [1]
  };

  const updatedSession: Session = {
    id: 1,
    name: 'Updated Yoga Session',
    description: 'Updated description',
    date: new Date(),
    teacher_id: 1,
    users: [1, 2, 3]
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:[
        HttpClientTestingModule
      ],
      providers:[
        SessionApiService
      ]
    });

    service = TestBed.inject(SessionApiService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  //==================================================================================
  //============= Test GET all sessions
  //==================================================================================
  it('should retrieve all sessions', (done) => {
    service.all().subscribe(sessions => {
      expect(sessions.length).toBe(2);            // Expect two sessions in response
      expect(sessions).toEqual(mockSessions);     // Expect response to match our mock data
      done();
    });

    const req = httpTestingController.expectOne('api/session');   // Setup an HTTP GET request to 'api/session'
    expect(req.request.method).toBe('GET');                       // Expect a GET request to 'api/session'
    req.flush(mockSessions);                                      // Simulate response
    httpTestingController.verify();                               // Verify that the HTTP request was made
  });

  //==================================================================================
  //============= Test GET session by ID
  //==================================================================================
  it('should retrieve details of a single session by ID', (done) => {
    const expectedSession = mockSessions[0];      // Use first mock session

    service.detail('1').subscribe(session => {
      expect(session.id).toBe(1);                 // Verify correct session returned
      expect(session).toEqual(expectedSession);   // Verify complete data match
      done();
    });

    const req = httpTestingController.expectOne('api/session/1');   // Setup an HTTP GET request to 'api/session/1'
    expect(req.request.method).toBe('GET');                         // Expect a GET request to 'api/session/1'
    req.flush(expectedSession);                                     // Simulate response
    httpTestingController.verify();                                 // Verify that the HTTP request was made
  });

  //==================================================================================
  //============= Test CREATE session
  //==================================================================================

  it('should create a new session', (done) => {

    service.create(newSession).subscribe(session => {
      expect(session).toEqual(newSession);            // Expect the response to match the new session
      expect(session.id).toBe(3);                     // Expect the session ID to be 3
      done();                                        
    });

    const req = httpTestingController.expectOne('api/session');  // Setup an HTTP POST request to 'api/session'
    expect(req.request.method).toBe('POST');                     // Expect a POST request to 'api/session'
    expect(req.request.body).toEqual(newSession);                // Expect the request body to match the new session
    req.flush(newSession);                                       // Simulate response
    httpTestingController.verify();                              // Verify that the HTTP request was made
  });

  //==================================================================================
  //============= Test DELETE session
  //==================================================================================

  it('should delete a session', (done) => {

    const deleteResponse = { message: 'Session deleted successfully' }; 

    service.delete('1').subscribe(response => {
      expect(response).toEqual(deleteResponse);                   // Expect the response to match the delete response
      done();
    });

    const req = httpTestingController.expectOne('api/session/1');  // Setup an HTTP DELETE request to 'api/session/1'
    expect(req.request.method).toBe('DELETE');                     // Expect a DELETE request to 'api/session/1'
    req.flush(deleteResponse);                                     // Simulate response
    httpTestingController.verify();                                // Verify that the HTTP request was made
  });

  //==================================================================================
  //==============================  Test for session update
  //==================================================================================

  it('should update an existing session', (done) => {

    service.update('1', updatedSession).subscribe({
      next: (session) => {
        expect(session).toEqual(updatedSession);            // Verify response matches update
        expect(session.name).toBe('Updated Yoga Session');  // Verify specific updates
        expect(session.users.length).toBe(3);               // Verify array updates
        done();
      },
    });

    const req = httpTestingController.expectOne('api/session/1');   // Setup an HTTP PUT request to 'api/session/1'
    expect(req.request.method).toBe('PUT');                         // Expect a PUT request to 'api/session/1'
    expect(req.request.body).toEqual(updatedSession);               // Expect the request body to match the updated session
    req.flush(updatedSession);                                      // Simulate response
    httpTestingController.verify();                                 // Verify that the HTTP request was made
  });

});