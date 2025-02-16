/// <reference types="Cypress" />

describe('Login page', () => {
  beforeEach(() => {
    cy.visit('/login');               // Before each test, visit the login page
  });

  //=================================================================================
  // Test successful login and logout flow
  //=================================================================================

  it('should let the user log in then log out', () => {

    // Mock the login API response
    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'userName',
        firstName: 'firstName',
        lastName: 'lastName',
        admin: true,
      },
    }).as('login');

    // Mock the session API response
    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
      },
      []
    ).as('session');

    // Fill the login form
    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type(`${'test!1234'}{enter}{enter}`);

    cy.url().should('include', '/sessions'); // Check if the user is redirected to the sessions page

    cy.wait('@login').then(({ response }) => {
      expect(response!.statusCode).to.equal(200); // Check if the login API response is successful
    });

    cy.wait('@session').then(({ response }) => {
      expect(response!.statusCode).to.equal(200); // Check if the session API response is successful
    });

    cy.get('.link').contains('Logout').click(); // Click on the logout button

    cy.url().should('include', '/'); // Check if the user is redirected to the home page
  });

  //=================================================================================
  // Test invalid login attempt
  //=================================================================================

  it('should return error if one of the inputs is not valid', () => {

    // Fill in form with invalid credentials
    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type(`'invalid' {enter}{enter}`);

    cy.get('.error').should('be.visible'); // Check if error message is displayed
  });

});