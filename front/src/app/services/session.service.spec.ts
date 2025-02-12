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
  //  Check the 'logIn' method
  //==================================================================================

  it('should handle login and update isLogged status', () => {
    const testUser: SessionInformation = {
      token: 'TestToken',
      type: 'Test',
      id: 1,
      username: 'testUser',
      firstName: 'Toto',
      lastName: 'Titi',
      admin: false
    };
    service.logIn(testUser);
    service.$isLogged().subscribe(isLogged => {
      expect(isLogged).toBe(true);
    });
    expect(service.sessionInformation).toEqual(testUser);
  });

  //==================================================================================
  //  Check the 'logOut' method
  //==================================================================================

  it('should handle logout and update isLogged status', () => {
    service.logOut();
    service.$isLogged().subscribe(isLogged => {
      expect(isLogged).toBe(false);
    });
    expect(service.sessionInformation).toBeUndefined();
  });
});
