describe('CRUD Farmácias - API Tests', () => {
  const baseUrl = Cypress.env('baseUrl') || 'http://localhost:9876';
  let createdFarmaciaId;
  let testFarmaciaData;

  before(() => {
    cy.login();
  });

  beforeEach(() => {
    // Reset test data
    testFarmaciaData = {
      razaoSocial: 'Farmácia Teste LTDA ' + Date.now(),
      nomeFantasia: 'Farmácia Teste ' + Date.now(),
      cnpj: generateValidCNPJ(),
      inscricaoEstadual: '123456789',
      inscricaoMunicipal: '987654321',
      endereco: 'Rua Teste, 123 - Centro, São Paulo - SP',
      telefoneContato: '11999887766',
      emailContato: 'teste@farmacia.com',
      status: 'ATIVA',
      ativo: true
    };
  });

  describe('CREATE - POST /api/farmacias', () => {
    it('should create a new farmacia with valid data', () => {
      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/farmacias`,
        body: testFarmaciaData
      }).then((response) => {
        expect(response.status).to.eq(201);
        expect(response.body).to.have.property('id');
        expect(response.body.razaoSocial).to.eq(testFarmaciaData.razaoSocial);
        expect(response.body.cnpj).to.eq(testFarmaciaData.cnpj);
        expect(response.body.status).to.eq('ATIVA');
        expect(response.body.ativo).to.be.true;

        createdFarmaciaId = response.body.id;
        Cypress.env('testFarmaciaId', createdFarmaciaId);
      });
    });

    it('should return 400 for invalid CNPJ', () => {
      const invalidData = {
        ...testFarmaciaData,
        cnpj: '12345678000100' // CNPJ inválido
      };

      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/farmacias`,
        body: invalidData,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
      });
    });

    it('should return 400 for duplicate CNPJ', () => {
      // Primeiro, criar uma farmácia
      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/farmacias`,
        body: testFarmaciaData
      }).then((response) => {
        expect(response.status).to.eq(201);
        createdFarmaciaId = response.body.id;

        // Tentar criar outra com o mesmo CNPJ
        cy.authenticatedRequest({
          method: 'POST',
          url: `${baseUrl}/api/farmacias`,
          body: {
            ...testFarmaciaData,
            razaoSocial: 'Outra Farmácia LTDA'
          },
          failOnStatusCode: false
        }).then((duplicateResponse) => {
          expect(duplicateResponse.status).to.eq(400);
        });
      });
    });

    it('should return 400 for missing required fields', () => {
      const incompleteData = {
        nomeFantasia: 'Farmácia Incompleta'
        // Faltam campos obrigatórios como razaoSocial, cnpj
      };

      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/farmacias`,
        body: incompleteData,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
      });
    });
  });

  describe('READ - GET endpoints', () => {
    before(() => {
      // Criar farmácia para testes de leitura
      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/farmacias`,
        body: testFarmaciaData
      }).then((response) => {
        createdFarmaciaId = response.body.id;
        Cypress.env('testFarmaciaId', createdFarmaciaId);
      });
    });

    it('should get all farmacias - GET /api/farmacias', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/farmacias`
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.be.an('array');
        expect(response.body.length).to.be.at.least(1);
        
        const farmacia = response.body.find(f => f.id === createdFarmaciaId);
        expect(farmacia).to.exist;
      });
    });

    it('should get farmacia by id - GET /api/farmacias/{id}', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/farmacias/${createdFarmaciaId}`
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.have.property('id', createdFarmaciaId);
        expect(response.body).to.have.property('razaoSocial');
        expect(response.body).to.have.property('cnpj');
        expect(response.body).to.have.property('status');
      });
    });

    it('should return 404 for non-existent farmacia', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/farmacias/99999`,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(404);
      });
    });

    it('should get farmacia by CNPJ - GET /api/farmacias/cnpj/{cnpj}', () => {
      // Wait a bit to ensure farmacia is indexed
      cy.wait(1000);
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/farmacias/cnpj/${testFarmaciaData.cnpj}`
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.have.property('cnpj', testFarmaciaData.cnpj);
        expect(response.body).to.have.property('id', createdFarmaciaId);
      });
    });

    it('should return 404 for non-existent CNPJ', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/farmacias/cnpj/12345678000199`,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(404);
      });
    });

    it('should get farmacias by status - GET /api/farmacias/status/{status}', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/farmacias/status/ATIVA`
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.be.an('array');
        
        if (response.body.length > 0) {
          response.body.forEach(farmacia => {
            expect(farmacia.status).to.eq('ATIVA');
          });
        }
      });
    });
  });

  describe('UPDATE - PUT /api/farmacias/{id}', () => {
    before(() => {
      // Criar farmácia para testes de atualização
      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/farmacias`,
        body: testFarmaciaData
      }).then((response) => {
        createdFarmaciaId = response.body.id;
      });
    });

    it('should update farmacia with valid data', () => {
      const updatedData = {
        ...testFarmaciaData,
        nomeFantasia: 'Farmácia Teste Atualizada',
        telefoneContato: '11888777666',
        emailContato: 'atualizado@farmacia.com'
      };

      cy.authenticatedRequest({
        method: 'PUT',
        url: `${baseUrl}/api/farmacias/${createdFarmaciaId}`,
        body: updatedData
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body.nomeFantasia).to.eq('Farmácia Teste Atualizada');
        expect(response.body.telefoneContato).to.eq('11888777666');
        expect(response.body.emailContato).to.eq('atualizado@farmacia.com');
        expect(response.body.id).to.eq(createdFarmaciaId);
      });
    });

    it('should return 400 for non-existent farmacia', () => {
      cy.authenticatedRequest({
        method: 'PUT',
        url: `${baseUrl}/api/farmacias/99999`,
        body: testFarmaciaData,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
      });
    });

    it('should return 400 for invalid update data', () => {
      const invalidData = {
        ...testFarmaciaData,
        cnpj: 'CNPJ_INVÁLIDO'
      };

      cy.authenticatedRequest({
        method: 'PUT',
        url: `${baseUrl}/api/farmacias/${createdFarmaciaId}`,
        body: invalidData,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
      });
    });
  });

  describe('PATCH Operations', () => {
    before(() => {
      // Criar farmácia para testes de PATCH
      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/farmacias`,
        body: testFarmaciaData
      }).then((response) => {
        createdFarmaciaId = response.body.id;
      });
    });

    it('should activate farmacia - PATCH /api/farmacias/{id}/ativar', () => {
      cy.authenticatedRequest({
        method: 'PATCH',
        url: `${baseUrl}/api/farmacias/${createdFarmaciaId}/ativar`
      }).then((response) => {
        expect(response.status).to.eq(200);
        
        // Verificar se foi ativada
        cy.authenticatedRequest({
          method: 'GET',
          url: `${baseUrl}/api/farmacias/${createdFarmaciaId}`
        }).then((getResponse) => {
          expect(getResponse.body.ativo).to.be.true;
          expect(getResponse.body.status).to.eq('ATIVA');
        });
      });
    });

    it('should deactivate farmacia - PATCH /api/farmacias/{id}/inativar', () => {
      cy.authenticatedRequest({
        method: 'PATCH',
        url: `${baseUrl}/api/farmacias/${createdFarmaciaId}/inativar`
      }).then((response) => {
        expect(response.status).to.eq(200);
        
        // Verificar se foi inativada
        cy.authenticatedRequest({
          method: 'GET',
          url: `${baseUrl}/api/farmacias/${createdFarmaciaId}`
        }).then((getResponse) => {
          expect(getResponse.body.ativo).to.be.false;
          expect(getResponse.body.status).to.eq('INATIVA');
        });
      });
    });

    it('should return 400 for non-existent farmacia on activate', () => {
      cy.authenticatedRequest({
        method: 'PATCH',
        url: `${baseUrl}/api/farmacias/99999/ativar`,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
      });
    });
  });

  describe('DELETE - DELETE /api/farmacias/{id}', () => {
    let farmaciaToDelete;

    beforeEach(() => {
      // Criar farmácia para ser deletada
      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/farmacias`,
        body: {
          ...testFarmaciaData,
          cnpj: generateValidCNPJ(),
          razaoSocial: 'Farmácia Para Deletar ' + Date.now()
        }
      }).then((response) => {
        farmaciaToDelete = response.body.id;
      });
    });

    it('should delete farmacia successfully', () => {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `${baseUrl}/api/farmacias/${farmaciaToDelete}`
      }).then((response) => {
        expect(response.status).to.eq(204);
        
        // Verificar se foi deletada
        cy.authenticatedRequest({
          method: 'GET',
          url: `${baseUrl}/api/farmacias/${farmaciaToDelete}`,
          failOnStatusCode: false
        }).then((getResponse) => {
          expect(getResponse.status).to.eq(404);
        });
      });
    });

    it('should return 400 for non-existent farmacia', () => {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `${baseUrl}/api/farmacias/99999`,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
      });
    });
  });

  describe('Authentication Tests', () => {
    it('should return 403 without authentication token', () => {
      cy.request({
        method: 'GET',
        url: `${baseUrl}/api/farmacias`,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(403);
      });
    });

    it('should return 403 with invalid token', () => {
      cy.request({
        method: 'GET',
        url: `${baseUrl}/api/farmacias`,
        headers: {
          'Authorization': 'Bearer invalid_token'
        },
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(403);
      });
    });
  });

  after(() => {
    // Cleanup - remover farmácia criada nos testes
    if (createdFarmaciaId) {
      cy.cleanupTestData('farmacias', createdFarmaciaId);
    }
  });

  // Função auxiliar para gerar CNPJ válido
  function generateValidCNPJ() {
    const timestamp = Date.now().toString();
    const base = timestamp.substring(timestamp.length - 8);
    return `${base}000100`;
  }
});