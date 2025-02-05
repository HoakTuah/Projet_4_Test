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
  //==============================  Test for navigation after successful login
  //=============================================================================================================

   it('should navigate to sessions page on successful login', () => {

    authServiceMock.login.mockReturnValue(of({ userId: '1', token: 'abc' }));     // Mock the login method of authService to simulate a successful login response
    component.form.setValue({ email: 'toto3@toto.com', password: 'test!1234' });  // Set valid credentials in the form
    component.submit();                                                           // Call the submit method

    expect(component.form.valid).toBeTruthy();                                    // Check if the form is valid  
    expect(authServiceMock.login).toHaveBeenCalled();                             // Check if the login method was called
    expect(routerMock.navigate).toHaveBeenCalledWith(['/sessions']);              // Check if you can navigate to the '/sessions' page after successful login
  });

  //=============================================================================================================
  //==============================  Test for error handling when login fails
  //=============================================================================================================

  it('should set onError to true on login failure', () => {
    authServiceMock.login.mockReturnValue(throwError(() => new Error('Invalid credentials')));     // Mock the login method of authService to simulate a login failure response
    component.form.setValue({ email: 'toto3@toto.com', password: 'test!1234' });                   // Set valid credentials in the form
    component.submit();                                                                            // Trigger the submit function
    fixture.detectChanges();                                                                       // Update view with validation feedback

    expect(component.form.valid).toBeTruthy();                                                    // Check if the form is valid               
    const errorMsg = fixture.debugElement.query(By.css('.error'));                                // Check if the error message is displayed
    expect(component.onError).toBeTruthy();                                                       // Check if the onError property is set to true
    expect(errorMsg.nativeElement.textContent).toContain('An error occurred');                    // Check if the error message contains the correct text
  });

  //=============================================================================================================
  //==============================  Check if a field is left empty
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
