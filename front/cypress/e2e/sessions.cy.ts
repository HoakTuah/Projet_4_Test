describe('Sessions page', () => {

    //=================================================================================       
    // Define constants
    //================================================================================= 
    
    const ADMIN_DETAILS = {
        token: 'jwt',
        type: 'Bearer',
        id: 1,
        username: 'yoga@studio.com',
        firstName: 'Admin',
        lastName: 'Admin',
        admin: true,
    };

    const TEACHERS_LIST = [
      {
        id: 1,
        lastName: 'Toto',
        firstName: 'Test',
        createdAt: '2025-01-12T15:33:42',
        updatedAt: '2025-08-12T15:33:42',
      },
      {
        id: 2,
        lastName: 'Titi',
        firstName: 'Test',
        createdAt: '2025-01-12T15:33:42',
        updatedAt: '2025-08-12T15:33:42',
      },
    ];

    const TEST_SESSION = {
        id: 1,
        name: 'Created Session For test',
        date: '2025-01-13T13:27:22.000+00:00',
        teacher_id: 1,
        description: 'Created Session For test description',
        users: [],
        createdAt: '2025-01-13T14:24:33',
        updatedAt: '2025-08-26T09:20:22',
      };
    
      const SESSIONS_LIST = [TEST_SESSION];
    
      const EDITED_TEST_SESSION = {
        ...TEST_SESSION,
        name: 'Edited Session For test',
        description: 'Edited Session For test description',
      };


    //=================================================================================       
    // Setup API mocks
    //================================================================================= 

    beforeEach(() => {

        //=================================================================================         
        // Mock the GET request for Teachers list and a single teacher and sessions list        
        //=================================================================================  

        cy.intercept('GET', '/api/teacher', {statusCode: 200,body: TEACHERS_LIST});     // Get all teachers
        cy.intercept('GET', '/api/teacher/*', {statusCode: 200,body: TEACHERS_LIST[0]}); // Get single teacher  

        cy.intercept('GET', '/api/session', (req) => {req.reply(SESSIONS_LIST);});
        cy.intercept('GET', '/api/session/*', (req) => {req.reply(TEST_SESSION);});

        //=================================================================================         
        // Mock CRUD operations for the session 
        //================================================================================= 
        
        
        // Mock the POST request for the session creation 
        cy.intercept('POST', '/api/session', (req) => {
            SESSIONS_LIST.push(TEST_SESSION);
            req.reply(TEST_SESSION);
        });

        // Mock the GET request for the session details 
        cy.intercept('GET', `/api/session/${TEST_SESSION.id}`, {statusCode: 200,body: TEST_SESSION});
  

        // Mock the PUT request for the session update   
        cy.intercept('PUT', `/api/session/${TEST_SESSION.id}`, (req) => {
            SESSIONS_LIST.splice(0, 1, EDITED_TEST_SESSION);
            req.reply(EDITED_TEST_SESSION);
        });   
        
        // Mock the DELETE request for the session deletion   
        cy.intercept('DELETE', `/api/session/${TEST_SESSION.id}`, (req) => {
            SESSIONS_LIST.splice(0, 1);
            req.reply(EDITED_TEST_SESSION);
        });

   
    });

    //=================================================================================       
    // Test as an admin user Create, Edit, Delete
    //=================================================================================

    describe('As an admin', () => {

      beforeEach(() => {
        // Visit the sessions page as an admin
        cy.visit('/login');
        cy.intercept('POST', '/api/auth/login', ADMIN_DETAILS);                     // Login as admin   
        cy.get('input[formControlName=email]').type('yoga@studio.com');
        cy.get('input[formControlName=password]').type('test!1234{enter}{enter}');
        cy.url().should('include', '/sessions');
      });
  
      it('Performs the following actions:', () => {

        // Check that we have 1 session displayed
        cy.get('mat-card:not(:contains("Rentals available"))').should('have.length', 1);
        cy.get('mat-card-title').should('contain', TEST_SESSION.name);
  
        //=================================================================================         
        // Create a new session
        //=================================================================================

        cy.get('button[mat-raised-button] span').contains('Create').click();
        cy.get('input[formControlName="name"]').type(TEST_SESSION.name);                    // Fill the name field  
        const formattedDate: string = TEST_SESSION.date.split('T')[0];                      // Format the date
        cy.get('input[formControlName="date"]').type(formattedDate);                        // Fill the date field  
        cy.get('mat-select[formControlName="teacher_id"]').click();                         // Click on the teacher field
        cy.get('mat-option').contains(TEACHERS_LIST[0].firstName).click();                  // Select the teacher    
        cy.get('textarea[formControlName="description"]').type(TEST_SESSION.description);   // Fill the description field
        cy.get('button[mat-raised-button]').contains('Save').click();                       // Save the session 
  
        // Check that the "Session created !" message is displayed and we have 2 sessions displayed     
        cy.get('snack-bar-container').contains('Session created !').should('exist');
        cy.get('snack-bar-container button span').contains('Close').click();
        cy.get('mat-card:not(:contains("Rentals available"))').should('have.length', 2);
  
        //=================================================================================             
        // Edit a session
        //=================================================================================

        cy.get('button[mat-raised-button] span').contains('Edit').click();
        cy.get('input[formControlName="name"]').clear().type(EDITED_TEST_SESSION.name);
        cy.get('textarea[formControlName="description"]').clear().type(EDITED_TEST_SESSION.description);
        cy.get('button[mat-raised-button]').contains('Save').click();
  
        // Check that the "Session updated !" message is displayed and the session name is updated
        cy.get('snack-bar-container').contains('Session updated !').should('exist');
        cy.get('snack-bar-container button span').contains('Close').click();
        cy.get('mat-card-title').should('contain', EDITED_TEST_SESSION.name);
  
        //=================================================================================         
        // Delete a session
        //=================================================================================

        cy.get('button').contains('Detail').click();
        cy.get('button').contains('Delete').click();
  
        // Check that the "Session deleted !" message is displayed and we have 1 session displayed  
        cy.get('snack-bar-container').contains('Session deleted !').should('exist');
        cy.get('snack-bar-container button span').contains('Close').click();
        cy.get('mat-card:not(:contains("Rentals available"))').should('have.length', 1);
       });
    });
  
  });