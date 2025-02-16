/// <reference types="Cypress" />

describe('Register page', () => {
  beforeEach(() => {
    cy.visit('/register');      // Before each test, visit the register page
  });

  //=================================================================================
  // Test successful registration flow
  //=================================================================================

  it('should let the user register successfully', () => {

    // Mock the register API response
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 201,
      body: {
        id: 1,
        firstName: 'firstName',
        lastName: 'lastName',
        email: 'yoga@studio.com'
      },
    }).as('register');

    // Fill the registration form
    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=firstName]').type('firstName');
    cy.get('input[formControlName=lastName]').type('lastName');
    cy.get('input[formControlName=password]').type(`${'test!1234'}{enter}{enter}`);

    cy.wait('@register').then(({ response }) => {
      expect(response!.statusCode).to.equal(201); // Check if the register API response is successful
    });

    cy.url().should('include', '/login'); // Check if the user is redirected to the login page
  });

  //=================================================================================
  // Test registration with existing email
  //=================================================================================

  it('should show error when registering with existing email', () => {
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 409,
      body: {
        message: 'Email already exists'
      }
    }).as('registerError');

    // Fill the registration form with existing email
    cy.get('input[formControlName=email]').type('existing@studio.com');
    cy.get('input[formControlName=firstName]').type('firstName');
    cy.get('input[formControlName=lastName]').type('lastName');
    cy.get('input[formControlName=password]').type(`${'test!1234'}{enter}{enter}`);

    cy.get('.error').should('be.visible');
  });

  //=================================================================================
  // Test form validation
  //=================================================================================

  it('should validate required fields', () => {
    // Test empty first name
    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=lastName]').type('lastName');
    cy.get('input[formControlName=password]').type('test!1234');
    cy.get('button[type=submit]').should('be.disabled');

    // Test empty last name
    cy.get('input[formControlName=firstName]').type('firstName');
    cy.get('input[formControlName=lastName]').clear();
    cy.get('button[type=submit]').should('be.disabled');

    // Test empty email
    cy.get('input[formControlName=lastName]').type('lastName');
    cy.get('input[formControlName=email]').clear();
    cy.get('button[type=submit]').should('be.disabled');

    // Test empty password
    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').clear();
    cy.get('button[type=submit]').should('be.disabled');
  });

  //=================================================================================
  // Test invalid email format
  //=================================================================================

  it('should validate email format', () => {
    // Fill form with invalid email
    cy.get('input[formControlName=email]').type('invalid-email');
    cy.get('input[formControlName=firstName]').type('firstName');
    cy.get('input[formControlName=lastName]').type('lastName');
    cy.get('input[formControlName=password]').type('test!1234');

    cy.get('button[type=submit]').should('be.disabled');
  });

});