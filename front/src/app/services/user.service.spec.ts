import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { UserService } from './user.service';

describe('UserService', () => {
  let service: UserService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:[HttpClientTestingModule], // Use HttpClientTestingModule instead of HttpClientModule to simulate HTTP requests
      providers:[UserService]
    });
    service = TestBed.inject(UserService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  //==================================================================================
  //==============================  Test to retrieve a user by ID
  //==================================================================================

  it('should retrieve a user by ID', (done) => {

    const mockUser = { id: '1'};    // Mock the user with id 1

    service.getById('1').subscribe({
      next: user => {
        console.log('Received user:', user);
        expect(user.id).toBe('1')
        done();
      },
    });
  
    const req = httpTestingController.expectOne('api/user/1');  // Setup an HTTP GET request to 'api/user/1'
    expect(req.request.method).toBe('GET');                     // Expect a GET request to 'api/user/1'
    req.flush(mockUser);                                        // simulating a successful server response with mockUser 
    httpTestingController.verify();                             // Verify that the HTTP request was made
  });

  //==================================================================================
  //==============================  Test to delete a user by ID
  //==================================================================================

  it('should delete a user by ID', (done) => {
    
    service.delete('1').subscribe(response => {
      expect(response).toEqual({ message: 'User deleted' });
      done();
    });

    const req = httpTestingController.expectOne('api/user/1');  // Setup an HTTP DELETE request to 'api/user/1'
    expect(req.request.method).toBe('DELETE');                  // Expect a DELETE request to 'api/user/1'
    req.flush({ message: 'User deleted' });                     // simulating a successful server response with { message: 'User deleted' }
    httpTestingController.verify();                             // Verify that the HTTP request was made
  });



});
