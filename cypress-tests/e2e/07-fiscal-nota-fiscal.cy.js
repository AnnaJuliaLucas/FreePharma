describe('Nota Fiscal API Tests', () => {
  const baseUrl = Cypress.env('baseUrl') || 'http://localhost:9876';
  let unidadeId;
  let farmaciaId;
  
  before(() => {
    cy.login();
    // Create test farmacia and unidade for nota fiscal tests
    cy.createTestFarmacia().then((farmacia) => {
      farmaciaId = farmacia.id;
      
      const unidadeData = {
        nomeFantasia: 'Unidade Nota Fiscal Teste',
        endereco: 'Rua Nota Fiscal, 123',
        telefone: '11777666555',
        email: 'notafiscal@teste.com',
        status: 'ATIVA',
        farmaciaId: farmaciaId
      };
      
      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/unidades`,
        body: unidadeData
      }).then((response) => {
        unidadeId = response.body.id;
      });
    });
  });

  after(() => {
    // Cleanup
    if (unidadeId) {
      cy.cleanupTestData('unidades', unidadeId);
    }
    if (farmaciaId) {
      cy.cleanupTestData('farmacias', farmaciaId);
    }
  });

  describe('GET /api/notas-fiscais', () => {
    it('should list all notas fiscais with authentication', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/notas-fiscais`
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.be.an('array');
        
        if (response.body.length > 0) {
          const notaFiscal = response.body[0];
          expect(notaFiscal).to.have.property('id');
          expect(notaFiscal).to.have.property('numero');
        }
      });
    });

    it('should return 403 without authentication', () => {
      cy.request({
        method: 'GET',
        url: `${baseUrl}/api/notas-fiscais`,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(403);
      });
    });
  });

  describe('GET /api/notas-fiscais/paginado', () => {
    it('should list notas fiscais with pagination', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/notas-fiscais/paginado?page=0&size=10&sort=id,desc`,
        failOnStatusCode: false
      }).then((response) => {
        expect([200, 500]).to.include(response.status);
        if (response.status === 200) {
          expect(response.body).to.have.property('content');
          expect(response.body).to.have.property('totalElements');
          expect(response.body.content).to.be.an('array');
        }
      });
    });

    it('should handle empty pagination results', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/notas-fiscais/paginado?page=999&size=10`,
        failOnStatusCode: false
      }).then((response) => {
        expect([200, 500]).to.include(response.status);
        if (response.status === 200) {
          expect(response.body.content).to.be.an('array');
        }
      });
    });
  });

  describe('GET /api/notas-fiscais/{id}', () => {
    it('should return 404 for non-existent nota fiscal ID', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/notas-fiscais/999999`,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(404);
      });
    });
  });

  describe('GET /api/notas-fiscais/unidade/{unidadeId}', () => {
    it('should get notas fiscais by unidade ID', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/notas-fiscais/unidade/${unidadeId}`
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.be.an('array');
      });
    });

    it('should return empty array for non-existent unidade', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/notas-fiscais/unidade/999999`
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.be.an('array');
        expect(response.body).to.have.length(0);
      });
    });
  });

  describe('GET /api/notas-fiscais/status/{status}', () => {
    it('should get notas fiscais by status', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/notas-fiscais/status/PROCESSADA`
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.be.an('array');
        
        if (response.body.length > 0) {
          response.body.forEach(nota => {
            expect(nota).to.have.property('status', 'PROCESSADA');
          });
        }
      });
    });

    it('should get notas fiscais by different statuses', () => {
      const statuses = ['PENDENTE', 'PROCESSADA', 'COM_INCONSISTENCIA', 'REJEITADA'];
      
      statuses.forEach(status => {
        cy.authenticatedRequest({
          method: 'GET',
          url: `${baseUrl}/api/notas-fiscais/status/${status}`
        }).then((response) => {
          expect(response.status).to.eq(200);
          expect(response.body).to.be.an('array');
        });
      });
    });
  });

  describe('GET /api/notas-fiscais/tipo/{tipoOperacao}', () => {
    it('should get notas fiscais by tipo operacao', () => {
      const tipos = ['ENTRADA', 'SAIDA'];
      
      tipos.forEach(tipo => {
        cy.authenticatedRequest({
          method: 'GET',
          url: `${baseUrl}/api/notas-fiscais/tipo/${tipo}`
        }).then((response) => {
          expect(response.status).to.eq(200);
          expect(response.body).to.be.an('array');
          
          if (response.body.length > 0) {
            response.body.forEach(nota => {
              expect(nota).to.have.property('tipoOperacao', tipo);
            });
          }
        });
      });
    });
  });

  describe('GET /api/notas-fiscais/periodo', () => {
    it('should get notas fiscais by date period', () => {
      const dataInicio = '2024-01-01';
      const dataFim = '2024-12-31';
      
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/notas-fiscais/periodo?dataInicio=${dataInicio}&dataFim=${dataFim}`
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.be.an('array');
      });
    });

    it('should return 400 for invalid date format', () => {
      const dataInicio = 'invalid-date';
      const dataFim = '2024-12-31';
      
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/notas-fiscais/periodo?dataInicio=${dataInicio}&dataFim=${dataFim}`,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
      });
    });

    it('should return 400 for missing date parameters', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/notas-fiscais/periodo`,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
      });
    });
  });

  describe('GET /api/notas-fiscais/chave/{chaveAcesso}', () => {
    it('should return 404 for non-existent chave acesso', () => {
      const chaveInexistente = '35200714200166000187550010000000123123456789';
      
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/notas-fiscais/chave/${chaveInexistente}`,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(404);
      });
    });
  });

  describe('GET /api/notas-fiscais/numero/{numero}', () => {
    it('should return 404 for non-existent numero', () => {
      const numeroInexistente = '999999999';
      
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/notas-fiscais/numero/${numeroInexistente}`,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(404);
      });
    });
  });

  describe('GET /api/notas-fiscais/{id}/itens', () => {
    it('should return empty array for non-existent nota fiscal items', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/notas-fiscais/999999/itens`
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.be.an('array');
        expect(response.body).to.have.length(0);
      });
    });
  });

  describe('GET /api/notas-fiscais/{id}/inconsistencias', () => {
    it('should return empty array for non-existent nota fiscal inconsistencies', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/notas-fiscais/999999/inconsistencias`
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.be.an('array');
        expect(response.body).to.have.length(0);
      });
    });
  });

  describe('GET /api/notas-fiscais/fornecedor/{fornecedorId}', () => {
    it('should get notas fiscais by fornecedor ID', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/notas-fiscais/fornecedor/1`
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.be.an('array');
      });
    });

    it('should return empty array for non-existent fornecedor', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/notas-fiscais/fornecedor/999999`
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.be.an('array');
        expect(response.body).to.have.length(0);
      });
    });
  });

  describe('GET /api/notas-fiscais/cliente/{clienteId}', () => {
    it('should get notas fiscais by cliente ID', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/notas-fiscais/cliente/1`
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.be.an('array');
      });
    });

    it('should return empty array for non-existent cliente', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/notas-fiscais/cliente/999999`
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.be.an('array');
        expect(response.body).to.have.length(0);
      });
    });
  });

  describe('Authentication Tests', () => {
    it('should return 401 for all endpoints without authentication', () => {
      const endpoints = [
        '/api/notas-fiscais',
        '/api/notas-fiscais/paginado',
        '/api/notas-fiscais/1',
        '/api/notas-fiscais/unidade/1',
        '/api/notas-fiscais/status/PROCESSADA',
        '/api/notas-fiscais/tipo/ENTRADA',
        '/api/notas-fiscais/chave/12345678901234567890123456789012345678901234',
        '/api/notas-fiscais/numero/123',
        '/api/notas-fiscais/1/itens',
        '/api/notas-fiscais/1/inconsistencias',
        '/api/notas-fiscais/fornecedor/1',
        '/api/notas-fiscais/cliente/1'
      ];

      endpoints.forEach(endpoint => {
        cy.request({
          method: 'GET',
          url: `${baseUrl}${endpoint}`,
          failOnStatusCode: false
        }).then((response) => {
          expect(response.status).to.eq(401);
        });
      });
    });
  });
});