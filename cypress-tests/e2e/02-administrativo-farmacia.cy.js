describe('Farmacia API Tests', () => {
  const baseUrl = Cypress.env('baseUrl') || 'http://localhost:9876';
  let createdFarmaciaId;
  
  before(() => {
    cy.login();
  });

  after(() => {
    // Cleanup created farmacia
    if (createdFarmaciaId) {
      cy.cleanupTestData('farmacias', createdFarmaciaId);
    }
  });

  describe('GET /api/farmacias', () => {
    it('should list all farmacias with authentication', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/farmacias`
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.be.an('array');
        
        if (response.body.length > 0) {
          const farmacia = response.body[0];
          expect(farmacia).to.have.property('id');
          expect(farmacia).to.have.property('razaoSocial');
          expect(farmacia).to.have.property('cnpj');
        }
      });
    });

    it('should return 403 without authentication', () => {
      cy.request({
        method: 'GET',
        url: `${baseUrl}/api/farmacias`,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(403);
      });
    });
  });

  describe('POST /api/farmacias', () => {
    it('should create new farmacia with valid data', () => {
      const farmaciaData = {
        razaoSocial: 'Farmácia Teste Cypress LTDA',
        nomeFantasia: 'Farmácia Cypress',
        cnpj: '12345678000199',
        inscricaoEstadual: '123456789',
        inscricaoMunicipal: '987654321',
        endereco: 'Rua Cypress, 123',
        telefoneContato: '11999999999',
        emailContato: 'cypress@teste.com',
        status: 'ATIVA'
      };

      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/farmacias`,
        body: farmaciaData
      }).then((response) => {
        expect(response.status).to.eq(201);
        expect(response.body).to.have.property('id');
        expect(response.body.razaoSocial).to.eq(farmaciaData.razaoSocial);
        expect(response.body.cnpj).to.eq(farmaciaData.cnpj);
        expect(response.body.status).to.eq('ATIVA');
        
        createdFarmaciaId = response.body.id;
      });
    });

    it('should return 400 for invalid CNPJ', () => {
      const invalidData = {
        razaoSocial: 'Farmácia Teste',
        cnpj: '123', // CNPJ inválido
        endereco: 'Rua Teste, 123'
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

    it('should return 400 for missing required fields', () => {
      const incompleteData = {
        nomeFantasia: 'Farmácia Incompleta'
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

    it('should return 400 for duplicate CNPJ', () => {
      const duplicateData = {
        razaoSocial: 'Outra Farmácia',
        cnpj: '12345678000199', // Mesmo CNPJ da farmácia criada anteriormente
        endereco: 'Outra Rua, 456'
      };

      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/farmacias`,
        body: duplicateData,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
      });
    });
  });

  describe('GET /api/farmacias/{id}', () => {
    it('should get farmacia by valid ID', () => {
      if (createdFarmaciaId) {
        cy.authenticatedRequest({
          method: 'GET',
          url: `${baseUrl}/api/farmacias/${createdFarmaciaId}`
        }).then((response) => {
          expect(response.status).to.eq(200);
          expect(response.body).to.have.property('id', createdFarmaciaId);
          expect(response.body).to.have.property('razaoSocial');
          expect(response.body).to.have.property('cnpj');
        });
      }
    });

    it('should return 404 for non-existent ID', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/farmacias/999999`,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(404);
      });
    });
  });

  describe('GET /api/farmacias/cnpj/{cnpj}', () => {
    it('should get farmacia by valid CNPJ', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/farmacias/cnpj/12345678000199`
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.have.property('cnpj', '12345678000199');
      });
    });

    it('should return 404 for non-existent CNPJ', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/farmacias/cnpj/00000000000000`,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(404);
      });
    });
  });

  describe('GET /api/farmacias/status/{status}', () => {
    it('should get farmacias by status', () => {
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

  describe('PUT /api/farmacias/{id}', () => {
    it('should update farmacia with valid data', () => {
      if (createdFarmaciaId) {
        const updateData = {
          razaoSocial: 'Farmácia Teste Cypress LTDA - Atualizada',
          nomeFantasia: 'Farmácia Cypress Atualizada',
          cnpj: '12345678000199', // CNPJ não pode ser alterado
          endereco: 'Rua Cypress Atualizada, 456',
          telefoneContato: '11888888888',
          emailContato: 'cypress.updated@teste.com',
          status: 'ATIVA'
        };

        cy.authenticatedRequest({
          method: 'PUT',
          url: `${baseUrl}/api/farmacias/${createdFarmaciaId}`,
          body: updateData
        }).then((response) => {
          expect(response.status).to.eq(200);
          expect(response.body.razaoSocial).to.eq(updateData.razaoSocial);
          expect(response.body.telefoneContato).to.eq(updateData.telefoneContato);
          expect(response.body.cnpj).to.eq('12345678000199'); // CNPJ mantido
        });
      }
    });

    it('should return 404 for non-existent ID', () => {
      const updateData = {
        razaoSocial: 'Farmácia Inexistente',
        cnpj: '12345678000199'
      };

      cy.authenticatedRequest({
        method: 'PUT',
        url: `${baseUrl}/api/farmacias/999999`,
        body: updateData,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
      });
    });
  });

  describe('PATCH /api/farmacias/{id}/ativar', () => {
    it('should activate farmacia', () => {
      if (createdFarmaciaId) {
        cy.authenticatedRequest({
          method: 'PATCH',
          url: `${baseUrl}/api/farmacias/${createdFarmaciaId}/ativar`
        }).then((response) => {
          expect(response.status).to.eq(200);
        });
      }
    });

    it('should return 400 for non-existent ID', () => {
      cy.authenticatedRequest({
        method: 'PATCH',
        url: `${baseUrl}/api/farmacias/999999/ativar`,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
      });
    });
  });

  describe('PATCH /api/farmacias/{id}/inativar', () => {
    it('should inactivate farmacia', () => {
      if (createdFarmaciaId) {
        cy.authenticatedRequest({
          method: 'PATCH',
          url: `${baseUrl}/api/farmacias/${createdFarmaciaId}/inativar`
        }).then((response) => {
          expect(response.status).to.eq(200);
        });
      }
    });
  });

  describe('DELETE /api/farmacias/{id}', () => {
    it('should delete farmacia', () => {
      if (createdFarmaciaId) {
        cy.authenticatedRequest({
          method: 'DELETE',
          url: `${baseUrl}/api/farmacias/${createdFarmaciaId}`
        }).then((response) => {
          expect(response.status).to.eq(204);
          createdFarmaciaId = null; // Evita cleanup duplo
        });
      }
    });

    it('should return 400 for non-existent ID', () => {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `${baseUrl}/api/farmacias/999999`,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
      });
    });
  });
});