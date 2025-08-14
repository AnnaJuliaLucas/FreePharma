// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************

// Comando para login e obtenção do token JWT
Cypress.Commands.add('login', (email = 'admin@freepharma.com', senha = '123456') => {
  cy.request({
    method: 'POST',
    url: `${Cypress.env('baseUrl')}/api/auth/login`,
    body: {
      email,
      senha
    }
  }).then((response) => {
    expect(response.status).to.eq(200);
    expect(response.body).to.have.property('accessToken');
    
    // Armazena o token para uso nos testes
    window.localStorage.setItem('accessToken', response.body.accessToken);
    window.localStorage.setItem('refreshToken', response.body.refreshToken);
    
    // Define como variável global do Cypress
    Cypress.env('accessToken', response.body.accessToken);
    
    return response.body;
  });
});

// Comando para fazer requisições autenticadas
Cypress.Commands.add('authenticatedRequest', (options) => {
  return cy.then(() => {
    let token = Cypress.env('accessToken') || window.localStorage.getItem('accessToken');
    
    if (!token || token === 'null') {
      // Se não há token, faz login automaticamente
      return cy.login().then(() => {
        token = Cypress.env('accessToken');
        return cy.request({
          ...options,
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json',
            ...options.headers
          }
        });
      });
    } else {
      return cy.request({
        ...options,
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
          ...options.headers
        }
      });
    }
  });
});

// Comando para logout
Cypress.Commands.add('logout', () => {
  const token = Cypress.env('accessToken');
  
  if (token) {
    cy.request({
      method: 'POST',
      url: `${Cypress.env('baseUrl')}/api/auth/logout`,
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
  }
  
  // Limpa os tokens armazenados
  window.localStorage.removeItem('accessToken');
  window.localStorage.removeItem('refreshToken');
  Cypress.env('accessToken', null);
});

// Comando para validar estrutura de resposta padrão
Cypress.Commands.add('validateResponseStructure', (response, expectedFields) => {
  expect(response.status).to.be.oneOf([200, 201]);
  expect(response.body).to.exist;
  
  if (Array.isArray(expectedFields)) {
    expectedFields.forEach(field => {
      expect(response.body).to.have.property(field);
    });
  }
});

// Comando para criar dados de teste para farmácia
Cypress.Commands.add('createTestFarmacia', () => {
  const farmaciaData = window.MockDataGenerator.generateFarmaciaData();
  
  return cy.authenticatedRequest({
    method: 'POST',
    url: `${Cypress.env('baseUrl')}/api/farmacias`,
    body: farmaciaData
  }).then((response) => {
    expect(response.status).to.eq(201);
    return response.body;
  });
});

// Comando para limpar dados de teste
Cypress.Commands.add('cleanupTestData', (resourceType, id) => {
  if (id) {
    cy.authenticatedRequest({
      method: 'DELETE',
      url: `${Cypress.env('baseUrl')}/api/${resourceType}/${id}`,
      failOnStatusCode: false
    });
  }
});

// Comando para upload de arquivo NFe
Cypress.Commands.add('uploadNFeFile', (fileName = 'nfe-test.xml') => {
  cy.fixture(fileName, 'base64').then(fileContent => {
    const blob = Cypress.Blob.base64StringToBlob(fileContent, 'text/xml');
    const formData = new FormData();
    formData.append('file', blob, fileName);
    
    const token = Cypress.env('accessToken');
    
    return cy.request({
      method: 'POST',
      url: `${Cypress.env('baseUrl')}/api/fiscal/importacao-nfe/xml`,
      headers: {
        'Authorization': `Bearer ${token}`
      },
      body: formData
    });
  });
});