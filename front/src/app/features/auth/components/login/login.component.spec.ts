import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { By } from '@angular/platform-browser';
import { expect } from '@jest/globals';
import { of, throwError } from 'rxjs';
import { AuthService } from '../../services/auth.service';
import { SessionService } from 'src/app/services/session.service';
import { Router } from '@angular/router';

import { LoginComponent } from './login.component';


describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authServiceMock: any;   // ADD mock for AuthService
  let routerMock: any;        // ADD mock for Router

  beforeEach(async () => {
    
    // Mock for login method
    authServiceMock = {
      login: jest.fn()    
    };

    // Mock for navigate method
    routerMock = {
      navigate: jest.fn() 
    };

    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      providers: [
        { provide: AuthService, useValue: authServiceMock },    // Mock Injector for AuthService
        { provide: Router, useValue: routerMock },              // Mock Injector for Router
        SessionService
      ],
      
      imports: [
        RouterTestingModule,
        BrowserAnimationsModule,
        HttpClientModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule]
    })
      .compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  //=============================================================================================================
  //===========Integration test : Test for navigation after successful login
  //=============================================================================================================

  it('should navigate to sessions page on successful login', () => {
    
    // Setup mocks

    authServiceMock.login.mockReturnValue(of({ userId: '1', token: 'test' }));  // Moock the Login method to return a successful login response
    const sessionService = jest.spyOn(TestBed.inject(SessionService),'logIn');  // Create a spy to watch if the SessionService.logIn method is called

    // Set form values for testing
    component.form.setValue({ 
        email: 'test@test.com', 
        password: 'test!1234' 
    });
    component.submit();                                               // Trigger the submit function 

    expect(component.form.valid).toBeTruthy();                        // Check if the form is valid
    expect(authServiceMock.login).toHaveBeenCalled();                 // Check if the login method was called
    expect(sessionService).toHaveBeenCalled();                        // Check if the SessionService.logIn method was called
    expect(routerMock.navigate).toHaveBeenCalledWith(['/sessions']);  // Check if the router navigated to the sessions page
});

  //=============================================================================================================
  //===========Integration test : Test for error handling when login fails
  //=============================================================================================================

  it('should set onError to true on login failure and check correct error message', () => {

    // Setup mocks
    authServiceMock.login.mockReturnValue(throwError(() => new Error('Invalid credentials')));     // Mock the login method of authService to simulate a login failure response
    
    // Set form values for testing
    component.form.setValue({
        email: 'test@test.com',
        password: 'test!1234'
    });                   

    component.submit();                                                                  // Trigger the submit function
    
    fixture.detectChanges();                                                             // Update view with validation feedback

    expect(component.form.valid).toBeTruthy();                                           // Check if the form is valid               
    const errorMsg = fixture.debugElement.query(By.css('.error'));                       // Find the error message element using the 'error' CSS class
    
    expect(component.onError).toBeTruthy();                                              // Check if the onError property is set to true
    expect(errorMsg.nativeElement.textContent).toContain('An error occurred');           // Check if the error message contains the correct text
  });

  //=============================================================================================================
  //=========== Unit test :  Check if a field is left empty
  //=============================================================================================================

   it('should display red color on empty and touched email and password field', () => {

    //===========================================================
    //======================== Email field test
    //===========================================================

    const emailInput = fixture.debugElement.query(By.css('input[formControlName="email"]'));
    emailInput.nativeElement.value = '';                            // Set invalid value
    emailInput.nativeElement.dispatchEvent(new Event('input'));     // Simulate user entering value
    emailInput.triggerEventHandler('blur', null);                   // Simulate user leaving the field (touching)
    fixture.detectChanges();                                        // Update view with validation feedback

    // Check if the error class is applied to Email
    expect(emailInput.nativeElement.classList.contains('ng-invalid')).toBeTruthy();
    expect(emailInput.nativeElement.classList.contains('ng-touched')).toBeTruthy();

    //===========================================================
    //======================== Password field test
    //===========================================================

    const passwordInput = fixture.debugElement.query(By.css('input[formControlName="password"]'));
    passwordInput.nativeElement.value = '';                         // Set invalid value
    passwordInput.nativeElement.dispatchEvent(new Event('input'));  // Simulate user entering value
    passwordInput.triggerEventHandler('blur', null);                // Simulate user leaving the field (touching)
    fixture.detectChanges();                                        // Update view with validation feedback

    // Check if the error class is applied to password
    expect(passwordInput.nativeElement.classList.contains('ng-invalid')).toBeTruthy();
    expect(passwordInput.nativeElement.classList.contains('ng-touched')).toBeTruthy();

   });



});
