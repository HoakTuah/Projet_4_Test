import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import { SessionInformation } from '../interfaces/sessionInformation.interface';

import { SessionService } from './session.service';

describe('SessionService', () => {
  let service: SessionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SessionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  //==================================================================================
  //  Unit test : logIn method then logOut method
  //==================================================================================

  it('should handle login and update isLogged status', () => {

    // Mock the sessionInformation object
    const testUser: SessionInformation = {
      token: 'TestToken',
      type: 'Test',
      id: 1,
      username: 'testUser',
      firstName: 'Toto',
      lastName: 'Titi',
      admin: false
    };

    // Login method test

    service.logIn(testUser);                              // Call the logIn method
    service.$isLogged().subscribe(isLogged => {
      expect(isLogged).toBe(true);                        // Check if the isLogged status is true
    });
    expect(service.sessionInformation).toEqual(testUser); //  Check if the sessionInformation is equal to the testUser

    // Logout method test

    service.logOut();                                     // Call the logOut method
    service.$isLogged().subscribe(isLogged => {
      expect(isLogged).toBe(false);                       // Check if the isLogged status is false
    });
    expect(service.sessionInformation).toBeUndefined();   // Check if the sessionInformation is undefined

  });

});
