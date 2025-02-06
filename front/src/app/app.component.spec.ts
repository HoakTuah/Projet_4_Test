import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatToolbarModule } from '@angular/material/toolbar';
import { RouterTestingModule } from '@angular/router/testing';
import { SessionService } from './services/session.service';
import { Router } from '@angular/router';
import { expect } from '@jest/globals';
import { of } from 'rxjs';

import { AppComponent } from './app.component';

describe('AppComponent', () => {

  let component: AppComponent; 
  let fixture: ComponentFixture<AppComponent>;
  let mockSessionService: any;                  // Mock for SessionService
  let mockRouter: any;                          // Mock for Router  
  
  beforeEach(async () => {

    mockSessionService = {
      $isLogged: jest.fn(),                     // Mock for $isLogged method
      logOut: jest.fn()                         // Mock for logOut method
    };

    mockRouter = {
      navigate: jest.fn()                       // Mock for navigate method
    };
    
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientModule,
        MatToolbarModule
      ],

      declarations: [
        AppComponent
      ],

      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: Router, useValue: mockRouter }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;

  });

  it('should create the app', () => {
    expect(component).toBeTruthy();
  });

  //=============================================================================================================
  //==============================  Test for Is logged Function
  //=============================================================================================================

  it('should return logged in status from $isLogged', () => {
    const expectedObservable = of(true);
    mockSessionService.$isLogged.mockReturnValue(expectedObservable);
    const result = component.$isLogged();


    expect(mockSessionService.$isLogged).toHaveBeenCalled();
    expect(result).toBe(expectedObservable);
  });

  //=============================================================================================================
  //==============================  Test for Logout Function
  //============================================================================================================= 

  it('should call logOut and navigate to home on logout', () => {
    component.logout();
    expect(mockSessionService.logOut).toHaveBeenCalled();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['']);
  });

});