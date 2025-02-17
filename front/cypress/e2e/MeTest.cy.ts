describe('Me Component', () => {
    
    //================================================================================= 
    // Define user Data for tests   
    //=================================================================================

    const TEST_PASSWORD = 'test!1234';
    
    const ADMIN_USER = {
          id: 1,
          username: 'yoga@studio.com',
          firstName: 'Admin',
          lastName: 'USER',
          email: 'yoga@studio.com',
          admin: true,
          createdAt: '2025-01-12T15:33:42',
          updatedAt: '2025-05-12T15:33:42'
      };

      const REGULAR_USER = {
        id: 2,
        username: 'user@studio.com',
        firstName: 'Toto',
        lastName: 'USER',
        email: 'user@studio.com',
        admin: false,
        createdAt: '2025-02-12T15:33:42',
        updatedAt: '2025-06-12T15:33:42'
      };

    //=================================================================================
    // Test as admin user
    //================================================================================= 
  
    describe('As admin user', () => {

      beforeEach(() => {
        // Setup API mocks for Admin user data and authentication   
        cy.visit('/login');
        cy.intercept('GET', '/api/user/*', ADMIN_USER).as('userData');
        cy.intercept('POST', '/api/auth/login', ADMIN_USER).as('userLogin');

        // Perform login with admin credentials
        cy.get('input[formControlName=email]').type(ADMIN_USER.email);
        cy.get('input[formControlName=password]').type(`${TEST_PASSWORD}{enter}{enter}`);
        cy.wait('@userLogin');                      // Wait for login response
        cy.get('span[routerlink="me"]').click();
  
      });
    
      //=================================================================================   
      // Test to display user information (admin)
      //=================================================================================
      
      it('should display correct admin information', () => {

        // Wait for user data to be fetched
        cy.wait('@userData').then(() => {
         
            // Verify page content
            cy.get('h1').should('contain.text', 'User information');
            cy.get('p').contains('Name:').should('contain.text', `Name: ${ADMIN_USER.firstName} ${ADMIN_USER.lastName}`);
            cy.get('p').contains('Email:').should('contain.text', `Email: ${ADMIN_USER.email}`);
            cy.get('p').contains('Create at').should('exist');
            cy.get('p').contains('Last update').should('exist');
  
            // Verify admin-specific elements
            cy.get('p').contains('You are admin').should('exist');
            cy.get('button').contains('Delete my account').should('not.exist');
        });
      });

      //=================================================================================
      // Test to navigate back when back button is clicked
      //=================================================================================   
  
      it('should navigate back when back button is clicked', () => {
        cy.get('button').contains('arrow_back').click();
        cy.url().should('include', '/sessions');
      });

    });

    //=================================================================================
    // Test as regular user 
    //================================================================================= 

    describe('As regular user', () => {

        beforeEach(() => {
            // Setup API mocks for regular user data and authentication   
            cy.visit('/login');
            cy.intercept('POST', '/api/auth/login', REGULAR_USER).as('userLogin');
            cy.intercept('GET', `/api/user/${REGULAR_USER.id}`, REGULAR_USER).as('userData');
    
            // Perform login with regular user credentials
            cy.get('input[formControlName=email]').type(REGULAR_USER.email);
            cy.get('input[formControlName=password]').type(`${TEST_PASSWORD}{enter}{enter}`);
            cy.wait('@userLogin');                      // Wait for login response  
            cy.get('span[routerlink="me"]').click();
        });

        //=================================================================================
        // Test to display user information (non admin)
        //================================================================================= 
    
        it('should display user information (non admin)', () => {

            // Wait for user data to be fetched 
            cy.wait('@userData').then(() => {

                // Verify page content  
                cy.get('h1').should('contain.text', 'User information');
                cy.get('p').contains('Name:').should('contain.text', `Name: ${REGULAR_USER.firstName} ${REGULAR_USER.lastName}`);
                cy.get('p').contains('Email:').should('contain.text', `Email: ${REGULAR_USER.email}`);
                cy.get('p').contains('Create at:').should('exist');
                cy.get('p').contains('Last update:').should('exist');
    
                // Verify non admin-specific elements
                cy.get('p').contains('You are admin').should('not.exist');
                cy.get('button').contains('Detail').should('exist');
            });
        });

        //=================================================================================
        // Test to allow user to delete account
        //================================================================================= 
    
        it('should allow user to delete account', () => {
            // Setup API mock for delete user
            cy.intercept('DELETE', `/api/user/${REGULAR_USER.id}`, {
                statusCode: 200,
                body: { message: 'User deleted successfully' }
            }).as('deleteUser');
    
             // Wait for user data and perform deletion
            cy.wait('@userData').then(() => {

                cy.get('button').contains('Detail').click();
                
                // Verify notification message and close button
                cy.get('simple-snack-bar') .should('be.visible').within(() => {
                    cy.contains('Your account has been deleted !').should('be.visible');
                    cy.get('button').contains('Close').click();
                });

                cy.wait('@deleteUser');
                cy.url().should('include', '/');
            });
        });
    
        //=================================================================================
        // Test to navigate back when back button is clicked
        //=================================================================================  

        it('should navigate back when back button is clicked', () => {
          cy.wait('@userData');
          cy.get('button').contains('arrow_back').click();
          cy.url().should('not.include', '/me');
        });
      });

      //=================================================================================
      // Test to display Not Found page
      //================================================================================= 

        
        describe('should display Not Found page', () => {
            it('should display the Not Found page', () => {
                cy.visit('/RandomPage');
                cy.url().should('include', '/404');
                cy.get('h1').should('contain.text', 'Page not found');
            });
        }); 

    });