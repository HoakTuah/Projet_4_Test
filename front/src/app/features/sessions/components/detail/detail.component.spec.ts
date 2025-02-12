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

  let windowSpy: jest.SpyInstance;

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

    windowSpy = jest.spyOn(window.history, 'back');
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  //=============================================================================
  //  Test Back Button
  //============================================================================= 

  it('should call window.history.back when back is called', () => {
    component.back();
    expect(windowSpy).toHaveBeenCalled();
  });

  //============================================================================= 
  //  Integration Test for Delete Button
  //============================================================================= 

  it('Delete button only visible if admin then trigger delete', () => {
    
    const deleteSpy = jest.spyOn(component, 'delete');
    const deleteButton = fixture.debugElement.query(By.css('button[mat-raised-button][color="warn"]'));
    expect(deleteButton).toBeTruthy();

    deleteButton.nativeElement.click();
    fixture.detectChanges();
    expect(deleteSpy).toHaveBeenCalled();
    expect(mockSnackBar.open).toHaveBeenCalledWith('Session deleted !', 'Close', { duration: 3000 });

    expect(mockRouter.navigate).toHaveBeenCalledWith(['sessions']);
  });

  //============================================================================= 
  //  Integration Test for Participation and Unparticipation
  //============================================================================= 

  it('should handle participation and then unparticipation correctly', () => {

    // Initial state for participation
    component.isAdmin = false;
    component.isParticipate = false;  // Initially not participating
    fixture.detectChanges();
  
    // Spy on participate and unParticipate methods
    const participateSpy = jest.spyOn(component, 'participate');
    const unParticipateSpy = jest.spyOn(component, 'unParticipate');
  
    // Simulate participation
    let participateButton = fixture.debugElement.query(By.css('button[mat-raised-button][color="primary"]'));
    expect(participateButton).toBeTruthy();
    participateButton.nativeElement.click();
    fixture.detectChanges();
  
    // Check participation actions
    expect(participateSpy).toHaveBeenCalled();
    expect(mockSessionApiService.participate).toHaveBeenCalledWith('1', '1');
    expect(mockSessionApiService.detail).toHaveBeenCalled();
  
    // Change state to reflect participation
    component.isParticipate = true;
    fixture.detectChanges();
  
    // Simulate unparticipation
    let unParticipateButton = fixture.debugElement.query(By.css('button[mat-raised-button][color="warn"]'));
    expect(unParticipateButton).toBeTruthy();
    unParticipateButton.nativeElement.click();
    fixture.detectChanges();
  
    // Check unparticipation actions
    expect(unParticipateSpy).toHaveBeenCalled();
    expect(mockSessionApiService.unParticipate).toHaveBeenCalledWith('1', '1');
    expect(mockSessionApiService.detail).toHaveBeenCalled();
  });


  
});

