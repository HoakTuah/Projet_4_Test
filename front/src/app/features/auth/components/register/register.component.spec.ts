import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { By } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { expect } from '@jest/globals';
import { RouterTestingModule } from '@angular/router/testing';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';

import { RegisterComponent } from './register.component';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let authServiceMock: any;
  let routerMock: any;

  beforeEach(async () => {


    authServiceMock = {
      register: jest.fn()                                   // Mock for register method
    };
    routerMock = {
      navigate: jest.fn()                                   // Mock for navigate method
    };

    await TestBed.configureTestingModule({
      declarations: [RegisterComponent],
      providers: [
        { provide: AuthService, useValue: authServiceMock }, // Mock Injector for AuthService
        { provide: Router, useValue: routerMock }            // Mock Injector for Router      
      ],
      imports: [
        BrowserAnimationsModule,
        HttpClientModule,
        ReactiveFormsModule,  
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
        RouterTestingModule
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  //=============================================================================================================
  //==============================  Function to test registration
  //============================================================================================================= 

  function testAccountCreation(formData: any, shouldSucceed: boolean) {

    authServiceMock.register.mockReturnValue(shouldSucceed ? of({}) : throwError(() => new Error('Failed to register')));
    component.form.setValue(formData);
    component.submit();
    fixture.detectChanges();

    if (shouldSucceed) {
      expect(authServiceMock.register).toHaveBeenCalledWith(formData);
      expect(routerMock.navigate).toHaveBeenCalledWith(['/login']);
    } else {
      expect(authServiceMock.register).toHaveBeenCalledWith(formData);
      expect(routerMock.navigate).not.toHaveBeenCalled();
      expect(component.onError).toBeTruthy();  
    }
  }

  it('should handle valid registration', () => {
    testAccountCreation({
      firstName: 'Toto',
      lastName: 'Titi',
      email: 'toto5@toto.com',
      password: 'test!1234'
    }, true);
  });

  it('should handle registration with missing fields', () => {
    testAccountCreation({
      firstName: '',
      lastName: 'Titi',
      email: 'toto5@toto.com',
      password: 'test!1234'
    }, false);
  });

  it('should handle registration with invalid email', () => {
    testAccountCreation({
      firstName: 'Toto',
      lastName: 'Titi',
      email: 'invalid-email',
      password: 'test!1234'
    }, false);
  });

  it('should handle registration short Password', () => {
    testAccountCreation({
      firstName: 'Toto',
      lastName: 'Titi',
      email: 'toto5@toto.com',
      password: 'Pa'
    }, false);
  });

  it('should handle registration too long Password', () => {
    testAccountCreation({
      firstName: 'Toto',
      lastName: 'Titi',
      email: 'toto5@toto.com',
      password: 'Password123!Password123!Password123!Password123!Password123!Password123!Password123!'
    }, false);
  });
  
//=============================================================================================================
//==============================  Function to test each field
//=============================================================================================================

function testInputField(fieldName: string, fieldValue: string, expectedValidity: boolean) {
  const input = fixture.debugElement.query(By.css(`input[formControlName="${fieldName}"]`));
  input.nativeElement.value = fieldValue;                       // Set value
  input.nativeElement.dispatchEvent(new Event('input'));        // Simulate user entering value
  input.triggerEventHandler('blur', null);                      // Simulate user leaving the field (touching)
  fixture.detectChanges();                                      // Update view with validation feedback

  // Check if the error class is applied
  expect(input.nativeElement.classList.contains('ng-invalid')).toBe(!expectedValidity); 
  expect(input.nativeElement.classList.contains('ng-touched')).toBeTruthy();
  }

//=============================================================================================================
//==============================  Test First Name field
//=============================================================================================================

  it('should validate First Name field correctly', () => {
    testInputField('firstName', '', false);     // Empty first name
    testInputField('firstName', 'Toto', true);  // Valid first name
  });

//=============================================================================================================
//==============================  Test Last Name field
//=============================================================================================================

it('should validate Last Name field correctly', () => {
  testInputField('lastName', '', false);     // Empty last name
  testInputField('lastName', 'Titi', true);  // Valid last name
});

//=============================================================================================================
//==============================  Test Email field
//=============================================================================================================

it('should validate Email field correctly', () => {
  testInputField('email', '', false);     // Empty email
  testInputField('email', 'invalid-email', false);  // Incorrect email format
  testInputField('email','toto5@toto.com', true);   // Valid email
});

//=============================================================================================================
//==============================  Test Password field
//=============================================================================================================

it('should validate Password field correctly', () => {
  testInputField('password', '', false);          // Empty password
  testInputField('password','test!1234', true);   // Valid password
});

});
