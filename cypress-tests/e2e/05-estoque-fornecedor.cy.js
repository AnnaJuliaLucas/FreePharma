describe('Fornecedor API Tests', () => {
  const baseUrl = Cypress.env('baseUrl') || 'http://localhost:9876';
  let createdFornecedorId;
  
  before(() => {
    cy.login();
  });

  after(() => {
    // Cleanup created fornecedor
    if (createdFornecedorId) {
      cy.cleanupTestData('fornecedores', createdFornecedorId);
    }
  });

  describe('GET /api/fornecedores', () => {
    it('should list all fornecedores with authentication', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/fornecedores`
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.be.an('array');
        
        if (response.body.length > 0) {
          const fornecedor = response.body[0];
          expect(fornecedor).to.have.property('id');
          expect(fornecedor).to.have.property('nomeFantasia');
          expect(fornecedor).to.have.property('cnpj');
        }
      });
    });

    it('should return 401 without authentication', () => {
      cy.request({
        method: 'GET',
        url: `${baseUrl}/api/fornecedores`,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(401);
      });
    });
  });

  describe('POST /api/fornecedores', () => {
    it('should create new fornecedor with valid data', () => {
      const fornecedorData = {
        nomeFantasia: 'Fornecedor Cypress Teste LTDA',
        razaoSocial: 'Fornecedor Cypress LTDA',
        cnpj: '12345678000177',
        inscricaoEstadual: '123456789',
        endereco: 'Rua Fornecedor Cypress, 123',
        telefone: '11999988877',
        email: 'fornecedor.cypress@teste.com',
        contato: 'João Silva',
        status: 'ATIVO'
      };

      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/fornecedores`,
        body: fornecedorData
      }).then((response) => {
        expect(response.status).to.eq(201);
        expect(response.body).to.have.property('id');
        expect(response.body.nomeFantasia).to.eq(fornecedorData.nomeFantasia);
        expect(response.body.cnpj).to.eq(fornecedorData.cnpj);
        expect(response.body.status).to.eq('ATIVO');
        
        createdFornecedorId = response.body.id;
      });
    });

    it('should return 400 for invalid CNPJ', () => {
      const invalidData = {
        nomeFantasia: 'Fornecedor Teste',
        cnpj: '123', // CNPJ inválido
        endereco: 'Rua Teste, 123'
      };

      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/fornecedores`,
        body: invalidData,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
      });
    });

    it('should return 400 for missing required fields', () => {
      const incompleteData = {
        razaoSocial: 'Fornecedor Incompleto'
      };

      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/fornecedores`,
        body: incompleteData,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
      });
    });

    it('should return 400 for invalid email format', () => {
      const invalidEmailData = {
        nomeFantasia: 'Fornecedor Email Inválido',
        cnpj: '22222222000122',
        endereco: 'Rua Teste, 123',
        email: 'email-invalido'
      };

      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/fornecedores`,
        body: invalidEmailData,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
      });
    });

    it('should return 400 for duplicate CNPJ', () => {
      const duplicateData = {
        nomeFantasia: 'Outro Fornecedor',
        cnpj: '12345678000177', // Mesmo CNPJ do fornecedor criado anteriormente
        endereco: 'Outra Rua, 456'
      };

      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/fornecedores`,
        body: duplicateData,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
      });
    });
  });

  describe('GET /api/fornecedores/{id}', () => {
    it('should get fornecedor by valid ID', () => {
      if (createdFornecedorId) {
        cy.authenticatedRequest({
          method: 'GET',
          url: `${baseUrl}/api/fornecedores/${createdFornecedorId}`
        }).then((response) => {
          expect(response.status).to.eq(200);
          expect(response.body).to.have.property('id', createdFornecedorId);
          expect(response.body).to.have.property('nomeFantasia');
          expect(response.body).to.have.property('cnpj');
        });
      }
    });

    it('should return 404 for non-existent ID', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/fornecedores/999999`,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(404);
      });
    });
  });

  describe('GET /api/fornecedores/cnpj/{cnpj}', () => {
    it('should get fornecedor by valid CNPJ', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/fornecedores/cnpj/12345678000177`
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.have.property('cnpj', '12345678000177');
      });
    });

    it('should return 404 for non-existent CNPJ', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/fornecedores/cnpj/00000000000000`,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(404);
      });
    });
  });

  describe('GET /api/fornecedores/status/{status}', () => {
    it('should get fornecedores by status', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/fornecedores/status/ATIVO`
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.be.an('array');
        
        if (response.body.length > 0) {
          response.body.forEach(fornecedor => {
            expect(fornecedor.status).to.eq('ATIVO');
          });
        }
      });
    });
  });

  describe('GET /api/fornecedores/ativos', () => {
    it('should get only active fornecedores', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/fornecedores/ativos`
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.be.an('array');
        
        if (response.body.length > 0) {
          response.body.forEach(fornecedor => {
            expect(fornecedor.status).to.eq('ATIVO');
          });
        }
      });
    });
  });

  describe('GET /api/fornecedores/buscar', () => {
    it('should search fornecedores by name', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/fornecedores/buscar?nome=Cypress`
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.be.an('array');
        
        if (response.body.length > 0) {
          response.body.forEach(fornecedor => {
            expect(fornecedor.nomeFantasia.toLowerCase()).to.include('cypress');
          });
        }
      });
    });

    it('should return empty array for non-existent name', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/fornecedores/buscar?nome=NomeInexistente123`
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.be.an('array');
        expect(response.body).to.have.length(0);
      });
    });
  });

  describe('PUT /api/fornecedores/{id}', () => {
    it('should update fornecedor with valid data', () => {
      if (createdFornecedorId) {
        const updateData = {
          nomeFantasia: 'Fornecedor Cypress Teste LTDA - Atualizado',
          razaoSocial: 'Fornecedor Cypress LTDA - Atualizada',
          cnpj: '12345678000177', // CNPJ não pode ser alterado
          endereco: 'Rua Fornecedor Cypress Atualizada, 456',
          telefone: '11888777666',
          email: 'fornecedor.cypress.updated@teste.com',
          contato: 'Maria Silva',
          status: 'ATIVO'
        };

        cy.authenticatedRequest({
          method: 'PUT',
          url: `${baseUrl}/api/fornecedores/${createdFornecedorId}`,
          body: updateData
        }).then((response) => {
          expect(response.status).to.eq(200);
          expect(response.body.nomeFantasia).to.eq(updateData.nomeFantasia);
          expect(response.body.telefone).to.eq(updateData.telefone);
          expect(response.body.cnpj).to.eq('12345678000177'); // CNPJ mantido
        });
      }
    });

    it('should return 400 for non-existent ID', () => {
      const updateData = {
        nomeFantasia: 'Fornecedor Inexistente',
        cnpj: '12345678000177'
      };

      cy.authenticatedRequest({
        method: 'PUT',
        url: `${baseUrl}/api/fornecedores/999999`,
        body: updateData,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
      });
    });
  });

  describe('PATCH /api/fornecedores/{id}/ativar', () => {
    it('should activate fornecedor', () => {
      if (createdFornecedorId) {
        cy.authenticatedRequest({
          method: 'PATCH',
          url: `${baseUrl}/api/fornecedores/${createdFornecedorId}/ativar`
        }).then((response) => {
          expect(response.status).to.eq(200);
        });
      }
    });

    it('should return 400 for non-existent ID', () => {
      cy.authenticatedRequest({
        method: 'PATCH',
        url: `${baseUrl}/api/fornecedores/999999/ativar`,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
      });
    });
  });

  describe('PATCH /api/fornecedores/{id}/inativar', () => {
    it('should inactivate fornecedor', () => {
      if (createdFornecedorId) {
        cy.authenticatedRequest({
          method: 'PATCH',
          url: `${baseUrl}/api/fornecedores/${createdFornecedorId}/inativar`
        }).then((response) => {
          expect(response.status).to.eq(200);
        });
      }
    });
  });

  describe('PATCH /api/fornecedores/{id}/bloquear', () => {
    it('should block fornecedor', () => {
      if (createdFornecedorId) {
        cy.authenticatedRequest({
          method: 'PATCH',
          url: `${baseUrl}/api/fornecedores/${createdFornecedorId}/bloquear`
        }).then((response) => {
          expect(response.status).to.eq(200);
        });
      }
    });
  });

  describe('DELETE /api/fornecedores/{id}', () => {
    it('should delete fornecedor', () => {
      if (createdFornecedorId) {
        cy.authenticatedRequest({
          method: 'DELETE',
          url: `${baseUrl}/api/fornecedores/${createdFornecedorId}`
        }).then((response) => {
          expect(response.status).to.eq(204);
          createdFornecedorId = null; // Evita cleanup duplo
        });
      }
    });

    it('should return 400 for non-existent ID', () => {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `${baseUrl}/api/fornecedores/999999`,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
      });
    });
  });
});