import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import {  ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';
import { SessionApiService } from '../../services/session-api.service';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';

import { FormComponent } from './form.component';

describe('FormComponent', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;
  let mockSessionApiService: any;
  let routerMock: any;
  let activatedRouteMock: any;

  const mockSessionService = {
    sessionInformation: {
      admin: true
    }
  } 

  const mockMatSnackBar = {
    open: jest.fn()
  };

  const mockSession = {
    name: 'Yoga',
    date: '2025-03-20',
    teacher_id: '1',
    description: 'Test'
  };

  beforeEach(async () => {

    mockSessionApiService = {
      detail: jest.fn().mockReturnValue(of({})),
      create: jest.fn().mockReturnValue(of({})),
      update: jest.fn().mockReturnValue(of({}))
    };

    routerMock = {
      navigate: jest.fn(),
      url: '/sessions/create'
    };

    await TestBed.configureTestingModule({

      imports: [
        RouterTestingModule.withRoutes([]),
        HttpClientModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule, 
        MatSnackBarModule,
        MatSelectModule,
        BrowserAnimationsModule
      ],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: SessionApiService, useValue: mockSessionApiService },
        { provide: Router, useValue: routerMock },
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        { provide: MatSnackBar, useValue: mockMatSnackBar },
        FormBuilder
      ],

      declarations: [FormComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  //========================================================================
  // Test for redirecting to /sessions when user is not admin
  //========================================================================

  it('should redirect when user is not admin', () => {

    mockSessionService.sessionInformation.admin = false;                           // Set admin to false
    component.ngOnInit();
    expect(routerMock.navigate).toHaveBeenCalledWith(['/sessions']);             // Check navigation to sessions
  });

  //========================================================================
  // Test for Create & Update including exit page
  //========================================================================

  it('It should test form Update & Create', () => {
    
    //========================================================================
    // Create Flow
    //========================================================================

    // Setup create flow
    component.onUpdate = false;
    
    // Setup spies for create flow
    const createSpy = jest.spyOn(mockSessionApiService, 'create').mockReturnValue(of(mockSession));  // Mock the create method
    const createSnackBarSpy = jest.spyOn(mockMatSnackBar, 'open');                                   // Mock the snack bar
    const createRouterSpy = jest.spyOn(routerMock, 'navigate');                                      // Mock the router
 
    // Prepare and submit form
    component.sessionForm?.patchValue(mockSession);
    component.submit();

    // Check all actions
    expect(createSpy).toHaveBeenCalledWith(mockSession);                                              // Check create API call
    expect(createSnackBarSpy).toHaveBeenCalledWith('Session created !','Close',{ duration: 3000 });   // Check success message
    expect(createRouterSpy).toHaveBeenCalledWith(['sessions']);                                       // Check navigation


    //========================================================================
    // Update Flow
    //========================================================================

    // Setup update flow
    component.onUpdate = true;
    component['id'] = '1';

    // Setup spies for update flow
    const sessionApiUpdateSpy = jest.spyOn(mockSessionApiService, 'update').mockReturnValue(of(mockSession)); // Mock the update method 
    const snackBarSpy = jest.spyOn(mockMatSnackBar, 'open');                                                  // Mock the snack bar
    const routerSpy = jest.spyOn(routerMock, 'navigate').mockImplementation(async () => true);                // Mock the router

    // Prepare and submit form
    component.sessionForm?.patchValue(mockSession);
    component.submit();

    // Check all actions
    expect(sessionApiUpdateSpy).toHaveBeenCalledWith('1', mockSession);                         // Check update API call
    expect(snackBarSpy).toHaveBeenCalledWith('Session updated !','Close',{ duration: 3000 });   // Check success message
    expect(routerSpy).toHaveBeenCalledWith(['sessions']);                                       // Check navigation
  });

});