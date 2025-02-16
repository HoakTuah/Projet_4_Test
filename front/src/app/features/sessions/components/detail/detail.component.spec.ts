import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { RouterTestingModule, } from '@angular/router/testing';
import { expect } from '@jest/globals'; 
import { SessionService } from '../../../../services/session.service';
import { SessionApiService } from '../../services/session-api.service';
import { Router } from '@angular/router';
import { ActivatedRoute } from '@angular/router';
import { TeacherService } from '../../../../services/teacher.service';
import { Teacher } from '../../../../interfaces/teacher.interface';
import { of } from 'rxjs';
import { DetailComponent } from './detail.component';
import { By } from '@angular/platform-browser';
import { MatIconModule } from '@angular/material/icon'; 
import { MatButtonModule } from '@angular/material/button'; 
import { MatCardModule } from '@angular/material/card';

describe('DetailComponent', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>; 
  let service: SessionService;
  let mockSessionApiService: any;
  let mockRouter: any;
  let mockActivatedRoute: any;
  let mockSnackBar: any;

  const mockSessionService = {
    sessionInformation: {
      admin: true,
      id: 1
    }
  }

  const mockTeacher: Teacher = {
    id: 1,
    lastName: "Toto",
    firstName: "Titi",
    createdAt: new Date('2024-01-01T09:00:00Z'),
    updatedAt: new Date('2025-01-01T09:00:00Z')
  };

  const mockTeacherService = {
    detail: jest.fn().mockReturnValue(of(mockTeacher))
  };

  beforeEach(async () => {
    mockSessionApiService = {
      delete: jest.fn().mockReturnValue(of({})),
      detail: jest.fn().mockReturnValue(of({
        id: '1',
        users: ['1'],
        teacher_id: '1'
      })),
      participate: jest.fn().mockReturnValue(of({})),
      unParticipate: jest.fn().mockReturnValue(of({}))
    };

    mockRouter = {
      navigate: jest.fn()
    };

    mockSnackBar = {
      open: jest.fn()
    };

    mockActivatedRoute = {
      snapshot: {
        paramMap: {
          get: jest.fn().mockReturnValue('1')
        }
      }
    };

    await TestBed.configureTestingModule({
      
      imports: [
        RouterTestingModule,
        HttpClientModule,
        MatSnackBarModule,
        ReactiveFormsModule,
        MatIconModule,
        MatButtonModule,
        MatCardModule

      ],

      declarations: [DetailComponent], 
      
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: TeacherService, useValue: mockTeacherService },
        { provide: SessionApiService, useValue: mockSessionApiService },
        { provide: Router, useValue: mockRouter },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: MatSnackBar, useValue: mockSnackBar }
      ],
    }).compileComponents();

    service = TestBed.inject(SessionService);
    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  //=============================================================================
  //  Test Back Button
  //============================================================================= 

  it('should call window.history.back when back is called', () => {
    const historySpy = jest.spyOn(window.history, 'back');  // Spy on window.history.back
    component.back();
    expect(historySpy).toHaveBeenCalled();                  // Verify that window.history.back was called
  });

  //============================================================================= 
  //  Integration Test for Delete Button
  //============================================================================= 

  it('Delete button only visible if admin then trigger delete', () => {
    
    const deleteSpy = jest.spyOn(component, 'delete');                                                    // Spy on delete method
    const deleteButton = fixture.debugElement.query(By.css('button[mat-raised-button][color="warn"]'));   // Get the delete button
    expect(deleteButton).toBeTruthy();                                                                    // Expect that the delete button is visible

    deleteButton.nativeElement.click();                                                                   // Simulate click on delete button 
    fixture.detectChanges();                                                                              // Trigger change detection 
    expect(deleteSpy).toHaveBeenCalled();                                                                 // Expect that the delete method was called
    expect(mockSnackBar.open).toHaveBeenCalledWith('Session deleted !', 'Close', { duration: 3000 });     // Expect that the snack bar was opened

    expect(mockRouter.navigate).toHaveBeenCalledWith(['sessions']);                                       // Expect that the router navigated to the sessions page
  });

  //============================================================================= 
  //  Integration Test for Participation and Unparticipation
  //============================================================================= 

  it('should handle participation then unparticipation', () => {

    // Initial state setup
    component.isAdmin = false;                                                                                  // Set admin status to false
    component.isParticipate = false;                                                                            // Set initial participation status to false
    fixture.detectChanges();                                                                                    // Update view with initial state
  
    // Setup method spies
    const participateSpy = jest.spyOn(component, 'participate');                                                // Create spy for participate method
    const unParticipateSpy = jest.spyOn(component, 'unParticipate');                                            // Create spy for unParticipate method
  
    // Test participation flow
    const participateButton = fixture.debugElement.query(By.css('button[mat-raised-button][color="primary"]')); // Find the participate button in DOM
    expect(participateButton).toBeTruthy();                                                                     // Expect that the participate button exists
    participateButton.nativeElement.click();                                                                    // Trigger click event on participate button
    fixture.detectChanges();                                                                                    // Update view after participation
  
    // Verify participation actions
    expect(participateSpy).toHaveBeenCalled();                                                                  // Expect that the participate method was triggered
    expect(mockSessionApiService.participate).toHaveBeenCalledWith('1', '1');                                   // Expect that the API call was made with correct session and user IDs
    expect(mockSessionApiService.detail).toHaveBeenCalled();                                                    // Expect that the session details were refreshed
  
    // Update participation state
    component.isParticipate = true;                                                                             // Update participation status to true
    fixture.detectChanges();                                                                                    // Update view with new participation state
  
    // Test unparticipation flow
    const unParticipateButton = fixture.debugElement.query(By.css('button[mat-raised-button][color="warn"]'));  // Find the unparticipate button in DOM (HTML)
    expect(unParticipateButton).toBeTruthy();                                                                   // Expect that the unparticipate button exists
    unParticipateButton.nativeElement.click();                                                                  // Trigger click event on unparticipate button
    fixture.detectChanges();                                                                                    // Update view after unparticipation
  
    // Verify unparticipation actions
    expect(unParticipateSpy).toHaveBeenCalled();                                                                // Expect that the   unParticipate method was triggered
    expect(mockSessionApiService.unParticipate).toHaveBeenCalledWith('1', '1');                                 // Expect that the API call was made with correct session and user IDs
    expect(mockSessionApiService.detail).toHaveBeenCalled();                                                    // Expect that the session details were refreshed
  });


  
});

