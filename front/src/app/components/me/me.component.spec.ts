import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { UserService } from '../../services/user.service';
import { SessionService } from 'src/app/services/session.service';
import { expect } from '@jest/globals';
import { Router } from '@angular/router';
import { of } from 'rxjs';
import { MeComponent } from './me.component';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('MeComponent', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;
  let mockUserService: any;
  let mockRouter: any;
  let mockMatSnackBar: any;

  const mockSessionService = {
    sessionInformation: {
      admin: false,
      id: 1
    },
    logOut: jest.fn()  // Ensure logOut is mocked
  }

  beforeEach(async () => {

    mockUserService = {
      getById: jest.fn().mockReturnValue(of({ id: '1', name: 'Toto' , email: 'toto@test.com' })),
      delete: jest.fn().mockReturnValue(of({}))
    };
    mockRouter = {
      navigate: jest.fn()
    };
    mockMatSnackBar = {
      open: jest.fn()
    };

    await TestBed.configureTestingModule({
      declarations: [MeComponent],
      imports: [
        MatSnackBarModule,
        HttpClientModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
        NoopAnimationsModule
      ],

      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: UserService, useValue: mockUserService },
        { provide: Router, useValue: mockRouter },
        { provide: MatSnackBar, useValue: mockMatSnackBar }
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  //=============================================================================================================
  //==============================  Test for ngOnInit
  //=============================================================================================================

  it('ngOnInit should fetch user data', () => {
    component.ngOnInit();
    expect(mockUserService.getById).toHaveBeenCalledWith('1');
    expect(component.user).toEqual({ id: '1', name: 'Toto',email: 'toto@test.com' });
  });

  //=============================================================================================================
  //==============================  Test for back Function
  //=============================================================================================================

  it('back should call window.history.back', () => {
    const historySpy = jest.spyOn(window.history, 'back');
    component.back();
    expect(historySpy).toHaveBeenCalled();
    historySpy.mockRestore();
  });

  //=============================================================================================================
  //==============================  Test for delete Function
  //=============================================================================================================

   it('delete should call userService.delete and handle response', () => {
     component.delete();
     expect(mockUserService.delete).toHaveBeenCalledWith('1');
     expect(mockMatSnackBar.open).toHaveBeenCalledWith("Your account has been deleted !", 'Close', { duration: 3000 });
     expect(mockSessionService.logOut).toHaveBeenCalled();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/']);

   });

});