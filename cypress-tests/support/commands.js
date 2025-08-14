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

// ===============================================
// COMANDOS PARA TESTES DE PERMISSÕES COM LOGS DETALHADOS
// ===============================================

// Custom command for API requests with detailed logging for video recording
Cypress.Commands.add('apiRequest', (method, url, options = {}) => {
  const fullUrl = url.startsWith('http') ? url : `${Cypress.env('baseUrl') || 'http://localhost:9876'}${url}`
  
  // Log request details with emojis for video visibility
  cy.log(`🔗 ${method.toUpperCase()} ${fullUrl}`)
  if (options.body) {
    cy.log('📤 Request Body:')
    cy.log(JSON.stringify(options.body, null, 2))
  }
  if (options.headers && options.headers.Authorization) {
    cy.log('🔑 Using Authorization Token')
  }

  return cy.request({
    method,
    url: fullUrl,
    failOnStatusCode: false,
    ...options
  }).then((response) => {
    // Log response details with clear visual indicators
    cy.log(`📥 Response Status: ${response.status}`)
    
    // Add status icon for video clarity
    if (response.status >= 200 && response.status < 300) {
      cy.log('✅ SUCCESS - Request completed successfully')
    } else if (response.status === 401) {
      cy.log('🚫 UNAUTHORIZED (401) - Invalid or missing token')
    } else if (response.status === 403) {
      cy.log('🛑 FORBIDDEN (403) - Access denied - insufficient permissions')
    } else if (response.status >= 400 && response.status < 500) {
      cy.log('⚠️ CLIENT ERROR - Bad request')
    } else if (response.status >= 500) {
      cy.log('❌ SERVER ERROR - Internal server issue')
    }
    
    // Log response body with formatting
    if (response.body) {
      cy.log('📄 Response Data:')
      cy.log(JSON.stringify(response.body, null, 2))
    }
    
    return response
  })
})

// Enhanced logging for test sections with visual separators
Cypress.Commands.add('logSection', (title, icon = '📋') => {
  cy.log(' ')
  cy.log(`${icon} ========================`)
  cy.log(`${icon} ${title}`)
  cy.log(`${icon} ========================`)
  cy.log(' ')
})

// Command for permission test authentication with detailed logging
Cypress.Commands.add('permissionAuth', (email, password, role) => {
  cy.logSection(`Authenticating as ${role}`, '🔐')
  
  return cy.apiRequest('POST', '/api/auth/login', {
    body: { email, password }
  }).then((response) => {
    if (response.status === 200 && response.body.accessToken) {
      const token = response.body.accessToken
      Cypress.env(`${role.toLowerCase()}Token`, token)
      
      cy.log(`✅ ${role} login successful`)
      cy.log(`🔑 Token captured: ${token.substring(0, 50)}...`)
      cy.log(`👤 User: ${response.body.usuario?.nome || 'N/A'}`)
      cy.log(`📧 Email: ${response.body.usuario?.email || 'N/A'}`)
    } else {
      cy.log(`❌ ${role} login failed`)
    }
    return response
  })
})

// Command for testing authorized access with visual feedback
Cypress.Commands.add('testAuthorizedAccess', (method, endpoint, role, expectedStatus = 200) => {
  const token = Cypress.env(`${role.toLowerCase()}Token`)
  
  cy.logSection(`Testing ${role} access to ${endpoint}`, '🧪')
  cy.log(`👑 Role: ${role}`)
  cy.log(`🎯 Expected Status: ${expectedStatus}`)
  
  return cy.apiRequest(method, endpoint, {
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  }).then((response) => {
    if (response.status === expectedStatus) {
      cy.log(`✅ Access test PASSED for ${role}`)
    } else {
      cy.log(`❌ Access test FAILED for ${role}`)
      cy.log(`Expected: ${expectedStatus}, Got: ${response.status}`)
    }
    return response
  })
})

// Command for testing unauthorized access with visual feedback
Cypress.Commands.add('testUnauthorizedAccess', (method, endpoint, role, expectedStatus = 403) => {
  const token = Cypress.env(`${role.toLowerCase()}Token`)
  
  cy.logSection(`Testing ${role} DENIED access to ${endpoint}`, '🚫')
  cy.log(`👤 Role: ${role}`)
  cy.log(`🎯 Expected Status: ${expectedStatus} (Access Denied)`)
  
  return cy.apiRequest(method, endpoint, {
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  }).then((response) => {
    if (response.status === expectedStatus) {
      cy.log(`✅ Access correctly DENIED for ${role}`)
      cy.log(`🛡️ Security working as expected`)
    } else {
      cy.log(`❌ Security BREACH - ${role} should not have access!`)
      cy.log(`Expected: ${expectedStatus}, Got: ${response.status}`)
    }
    return response
  })
})