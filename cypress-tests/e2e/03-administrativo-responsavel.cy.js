describe('Responsavel API Tests', () => {
  const baseUrl = Cypress.env('baseUrl') || 'http://localhost:9876';
  let createdResponsavelId;
  let farmaciaId;
  
  before(() => {
    cy.login();
    // Create a farmacia first for responsavel tests
    cy.createTestFarmacia().then((farmacia) => {
      farmaciaId = farmacia.id;
    });
  });

  after(() => {
    // Cleanup
    if (createdResponsavelId) {
      cy.cleanupTestData('responsaveis', createdResponsavelId);
    }
    if (farmaciaId) {
      cy.cleanupTestData('farmacias', farmaciaId);
    }
  });

  describe('GET /api/responsaveis', () => {
    it('should list all responsaveis with authentication', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/responsaveis`
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.be.an('array');
      });
    });

    it('should return 403 without authentication', () => {
      cy.request({
        method: 'GET',
        url: `${baseUrl}/api/responsaveis`,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(403);
      });
    });
  });

  describe('POST /api/responsaveis', () => {
    it('should create new responsavel with valid data', () => {
      const responsavelData = window.MockDataGenerator.generateResponsavelData(farmaciaId);

      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/responsaveis`,
        body: responsavelData
      }).then((response) => {
        expect(response.status).to.eq(201);
        expect(response.body).to.have.property('id');
        expect(response.body.nome).to.eq(responsavelData.nome);
        expect(response.body.cpf).to.eq(responsavelData.cpf);
        expect(response.body.email).to.eq(responsavelData.email);
        
        createdResponsavelId = response.body.id;
      });
    });

    it('should return 400 for invalid CPF', () => {
      const invalidData = {
        nome: 'Dr. Teste',
        cpfCnpj: '123', // CPF inválido
        farmaciaId: farmaciaId
      };

      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/responsaveis`,
        body: invalidData,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
      });
    });

    it('should return 400 for missing required fields', () => {
      const incompleteData = {
        cpfCnpj: window.MockDataGenerator.generateCPF()
      };

      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/responsaveis`,
        body: incompleteData,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
      });
    });

    it('should return 400 for invalid email format', () => {
      const invalidEmailData = {
        nome: 'Dr. Teste Email',
        cpfCnpj: window.MockDataGenerator.generateCPF(),
        email: 'email-invalido',
        farmaciaId: farmaciaId
      };

      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/responsaveis`,
        body: invalidEmailData,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
      });
    });

    it('should return 400 for duplicate CPF', () => {
      const duplicateData = window.MockDataGenerator.generateResponsavelData(farmaciaId);

      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/responsaveis`,
        body: duplicateData,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
      });
    });

    it('should return 400 for non-existent farmacia', () => {
      const invalidFarmaciaData = {
        nome: 'Dr. Farmácia Inexistente',
        cpfCnpj: window.MockDataGenerator.generateCPF(),
        farmaciaId: 999999
      };

      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/responsaveis`,
        body: invalidFarmaciaData,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
      });
    });
  });

  describe('GET /api/responsaveis/{id}', () => {
    it('should get responsavel by valid ID', () => {
      if (createdResponsavelId) {
        cy.authenticatedRequest({
          method: 'GET',
          url: `${baseUrl}/api/responsaveis/${createdResponsavelId}`
        }).then((response) => {
          expect(response.status).to.eq(200);
          expect(response.body).to.have.property('id', createdResponsavelId);
          expect(response.body).to.have.property('nome');
          expect(response.body).to.have.property('cpf');
        });
      }
    });

    it('should return 404 for non-existent ID', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/responsaveis/999999`,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(404);
      });
    });
  });

  describe('GET /api/responsaveis/cpf/{cpf}', () => {
    it('should get responsavel by valid CPF', () => {
      // Usar um CPF que sabemos que existe (buscar primeiro responsável da lista)
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/responsaveis`
      }).then((listResponse) => {
        if (listResponse.body.length > 0) {
          const primeiroResponsavel = listResponse.body[0];
          cy.authenticatedRequest({
            method: 'GET',
            url: `${baseUrl}/api/responsaveis/cpf/${primeiroResponsavel.cpfCnpj}`
          }).then((response) => {
            expect(response.status).to.eq(200);
            expect(response.body).to.have.property('cpfCnpj', primeiroResponsavel.cpfCnpj);
          });
        }
      });
    });

    it('should return 404 for non-existent CPF', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/responsaveis/cpf/00000000000`,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(404);
      });
    });
  });

  describe('GET /api/responsaveis/email/{email}', () => {
    it('should get responsavel by valid email', () => {
      // Usar um email que sabemos que existe (buscar primeiro responsável da lista)
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/responsaveis`
      }).then((listResponse) => {
        if (listResponse.body.length > 0) {
          const primeiroResponsavel = listResponse.body[0];
          if (primeiroResponsavel.email) {
            cy.authenticatedRequest({
              method: 'GET',
              url: `${baseUrl}/api/responsaveis/email/${primeiroResponsavel.email}`
            }).then((response) => {
              expect(response.status).to.eq(200);
              expect(response.body).to.have.property('email', primeiroResponsavel.email);
            });
          }
        }
      });
    });

    it('should return 404 for non-existent email', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/responsaveis/email/inexistente@teste.com`,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(404);
      });
    });
  });

  describe('GET /api/responsaveis/farmacia/{farmaciaId}', () => {
    it('should get responsaveis by farmacia ID', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/responsaveis/farmacia/${farmaciaId}`
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.be.an('array');
      });
    });
  });

  describe('GET /api/responsaveis/ativos', () => {
    it('should get only active responsaveis', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/responsaveis/ativos`
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.be.an('array');
      });
    });
  });

  describe('PUT /api/responsaveis/{id}', () => {
    it('should update responsavel with valid data', () => {
      if (createdResponsavelId) {
        const updateData = {
          nome: 'Dr. João Silva Cypress - Atualizado',
          cpfCnpj: '98765432100',
          email: 'joao.updated@teste.com',
          telefone: '11999888777',
          numeroConselho: 'CRF-SP 54321',
          farmacia: {
            id: farmaciaId
          },
          ativo: true
        };

        cy.authenticatedRequest({
          method: 'PUT',
          url: `${baseUrl}/api/responsaveis/${createdResponsavelId}`,
          body: updateData
        }).then((response) => {
          expect(response.status).to.eq(200);
          expect(response.body.nome).to.eq(updateData.nome);
          expect(response.body.email).to.eq(updateData.email);
          expect(response.body.telefone).to.eq(updateData.telefone);
        });
      }
    });

    it('should return 400 for non-existent ID', () => {
      const updateData = {
        nome: 'Dr. Inexistente',
        cpfCnpj: '98765432100'
      };

      cy.authenticatedRequest({
        method: 'PUT',
        url: `${baseUrl}/api/responsaveis/999999`,
        body: updateData,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
      });
    });
  });

  describe('PATCH /api/responsaveis/{id}/ativar', () => {
    it('should activate responsavel', () => {
      if (createdResponsavelId) {
        cy.authenticatedRequest({
          method: 'PATCH',
          url: `${baseUrl}/api/responsaveis/${createdResponsavelId}/ativar`
        }).then((response) => {
          expect(response.status).to.eq(200);
        });
      }
    });

    it('should return 400 for non-existent ID', () => {
      cy.authenticatedRequest({
        method: 'PATCH',
        url: `${baseUrl}/api/responsaveis/999999/ativar`,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
      });
    });
  });

  describe('PATCH /api/responsaveis/{id}/inativar', () => {
    it('should inactivate responsavel', () => {
      if (createdResponsavelId) {
        cy.authenticatedRequest({
          method: 'PATCH',
          url: `${baseUrl}/api/responsaveis/${createdResponsavelId}/inativar`
        }).then((response) => {
          expect(response.status).to.eq(200);
        });
      }
    });
  });

  describe('DELETE /api/responsaveis/{id}', () => {
    it('should delete responsavel', () => {
      if (createdResponsavelId) {
        cy.authenticatedRequest({
          method: 'DELETE',
          url: `${baseUrl}/api/responsaveis/${createdResponsavelId}`
        }).then((response) => {
          expect(response.status).to.eq(204);
          createdResponsavelId = null; // Evita cleanup duplo
        });
      }
    });

    it('should return 400 for non-existent ID', () => {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `${baseUrl}/api/responsaveis/999999`,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
      });
    });
  });
});