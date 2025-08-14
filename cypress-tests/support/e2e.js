// ***********************************************************
// This example support/e2e.js is processed and
// loaded automatically before your test files.
//
// This is a great place to put global configuration and
// behavior that modifies Cypress.
//
// You can change the location of this file or turn off
// automatically serving support files with the
// 'supportFile' configuration option.
//
// You can read more here:
// https://on.cypress.io/configuration
// ***********************************************************

// Import commands.js using ES2015 syntax:
import './commands'

// Import mock data generator
import MockDataGenerator from './mockData'

// Make MockDataGenerator available globally
window.MockDataGenerator = MockDataGenerator;

// Alternatively you can use CommonJS syntax:
// require('./commands')

// Global configuration
Cypress.on('uncaught:exception', (err, runnable) => {
  // returning false here prevents Cypress from
  // failing the test on uncaught exceptions
  console.log('Uncaught exception:', err);
  return false;
});

// Set base URL if not defined in cypress config
if (!Cypress.env('baseUrl')) {
  Cypress.env('baseUrl', 'http://localhost:9876');
}

// Before each test, clear any stored tokens
beforeEach(() => {
  // Clear localStorage
  cy.window().then((win) => {
    win.localStorage.clear();
  });
  
  // Clear Cypress env variables
  Cypress.env('accessToken', null);
});