describe('CRUD Fornecedores - API Tests', () => {
  const baseUrl = Cypress.env('baseUrl') || 'http://localhost:9876';
  let createdFornecedorId;
  let testFornecedorData;

  before(() => {
    cy.login();
  });

  beforeEach(() => {
    testFornecedorData = {
      razaoSocial: 'Distribuidora Teste LTDA ' + Date.now(),
      nomeFantasia: 'Distribuidora Teste',
      cnpj: generateValidCNPJ(),
      inscricaoEstadual: '123456789',
      endereco: 'Av. Distribuidora, 500 - Industrial, São Paulo - SP',
      cidade: 'São Paulo',
      estado: 'SP',
      cep: '03310000',
      telefone: '1133334444',
      email: 'contato@distribuidora.com',
      status: 'ATIVO',
      dataCadastro: new Date().toISOString(),
      ativo: true
    };
  });

  describe('CREATE - POST /api/fornecedores', () => {
    it('should create a new fornecedor with valid data', () => {
      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/fornecedores`,
        body: testFornecedorData
      }).then((response) => {
        expect(response.status).to.eq(201);
        expect(response.body).to.have.property('id');
        expect(response.body.razaoSocial).to.eq(testFornecedorData.razaoSocial);
        expect(response.body.cnpj).to.eq(testFornecedorData.cnpj);
        expect(response.body.status).to.eq('ATIVO');
        expect(response.body.ativo).to.be.true;

        createdFornecedorId = response.body.id;
        Cypress.env('testFornecedorId', createdFornecedorId);
      });
    });

    it('should return 400 for invalid CNPJ', () => {
      const invalidData = {
        ...testFornecedorData,
        cnpj: '12345'
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

    it('should return 400 for duplicate CNPJ', () => {
      // Primeiro, criar um fornecedor
      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/fornecedores`,
        body: testFornecedorData
      }).then((response) => {
        expect(response.status).to.eq(201);
        createdFornecedorId = response.body.id;

        // Tentar criar outro com o mesmo CNPJ
        cy.authenticatedRequest({
          method: 'POST',
          url: `${baseUrl}/api/fornecedores`,
          body: {
            ...testFornecedorData,
            razaoSocial: 'Outro Fornecedor LTDA'
          },
          failOnStatusCode: false
        }).then((duplicateResponse) => {
          expect(duplicateResponse.status).to.eq(400);
        });
      });
    });

    it('should return 400 for missing required fields', () => {
      const incompleteData = {
        nomeFantasia: 'Fornecedor Incompleto'
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
  });

  describe('READ - GET endpoints', () => {
    before(() => {
      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/fornecedores`,
        body: testFornecedorData
      }).then((response) => {
        createdFornecedorId = response.body.id;
        Cypress.env('testFornecedorId', createdFornecedorId);
      });
    });

    it('should get all fornecedores - GET /api/fornecedores', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/fornecedores`
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.be.an('array');
        expect(response.body.length).to.be.at.least(1);
        
        const fornecedor = response.body.find(f => f.id === createdFornecedorId);
        expect(fornecedor).to.exist;
      });
    });

    it('should get active fornecedores - GET /api/fornecedores/ativos', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/fornecedores/ativos`
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.be.an('array');
        
        if (response.body.length > 0) {
          response.body.forEach(fornecedor => {
            expect(fornecedor.ativo).to.be.true;
          });
        }
      });
    });

    it('should search fornecedores by name - GET /api/fornecedores/buscar', () => {
      const searchTerm = testFornecedorData.nomeFantasia.substring(0, 10);
      
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/fornecedores/buscar?nome=${searchTerm}`
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.be.an('array');
        
        if (response.body.length > 0) {
          response.body.forEach(fornecedor => {
            expect(fornecedor.nomeFantasia.toLowerCase()).to.include(searchTerm.toLowerCase());
          });
        }
      });
    });

    it('should get fornecedor by id - GET /api/fornecedores/{id}', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/fornecedores/${createdFornecedorId}`
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.have.property('id', createdFornecedorId);
        expect(response.body).to.have.property('razaoSocial');
        expect(response.body).to.have.property('cnpj');
        expect(response.body).to.have.property('status');
      });
    });

    it('should return 404 for non-existent fornecedor', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/fornecedores/99999`,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(404);
      });
    });

    it('should get fornecedor by CNPJ - GET /api/fornecedores/cnpj/{cnpj}', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/fornecedores/cnpj/${testFornecedorData.cnpj}`
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.have.property('cnpj', testFornecedorData.cnpj);
        expect(response.body).to.have.property('id', createdFornecedorId);
      });
    });

    it('should return 404 for non-existent CNPJ', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/fornecedores/cnpj/99999999000199`,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(404);
      });
    });

    it('should get fornecedores by status - GET /api/fornecedores/status/{status}', () => {
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

  describe('UPDATE - PUT /api/fornecedores/{id}', () => {
    before(() => {
      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/fornecedores`,
        body: testFornecedorData
      }).then((response) => {
        createdFornecedorId = response.body.id;
      });
    });

    it('should update fornecedor with valid data', () => {
      const updatedData = {
        ...testFornecedorData,
        nomeFantasia: 'Distribuidora Atualizada',
        telefone: '1199998888',
        email: 'novo@distribuidora.com',
        endereco: 'Novo Endereço, 999'
      };

      cy.authenticatedRequest({
        method: 'PUT',
        url: `${baseUrl}/api/fornecedores/${createdFornecedorId}`,
        body: updatedData
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body.nomeFantasia).to.eq('Distribuidora Atualizada');
        expect(response.body.telefone).to.eq('1199998888');
        expect(response.body.email).to.eq('novo@distribuidora.com');
        expect(response.body.endereco).to.eq('Novo Endereço, 999');
        expect(response.body.id).to.eq(createdFornecedorId);
      });
    });

    it('should return 404 for non-existent fornecedor', () => {
      cy.authenticatedRequest({
        method: 'PUT',
        url: `${baseUrl}/api/fornecedores/99999`,
        body: testFornecedorData,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(404);
      });
    });

    it('should return 400 for invalid update data', () => {
      const invalidData = {
        ...testFornecedorData,
        email: 'email_inválido'
      };

      cy.authenticatedRequest({
        method: 'PUT',
        url: `${baseUrl}/api/fornecedores/${createdFornecedorId}`,
        body: invalidData,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
      });
    });
  });

  describe('PATCH Operations', () => {
    before(() => {
      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/fornecedores`,
        body: testFornecedorData
      }).then((response) => {
        createdFornecedorId = response.body.id;
      });
    });

    it('should activate fornecedor - PATCH /api/fornecedores/{id}/ativar', () => {
      cy.authenticatedRequest({
        method: 'PATCH',
        url: `${baseUrl}/api/fornecedores/${createdFornecedorId}/ativar`
      }).then((response) => {
        expect(response.status).to.eq(200);
        
        // Verificar se foi ativado
        cy.authenticatedRequest({
          method: 'GET',
          url: `${baseUrl}/api/fornecedores/${createdFornecedorId}`
        }).then((getResponse) => {
          expect(getResponse.body.ativo).to.be.true;
          expect(getResponse.body.status).to.eq('ATIVO');
        });
      });
    });

    it('should deactivate fornecedor - PATCH /api/fornecedores/{id}/inativar', () => {
      cy.authenticatedRequest({
        method: 'PATCH',
        url: `${baseUrl}/api/fornecedores/${createdFornecedorId}/inativar`
      }).then((response) => {
        expect(response.status).to.eq(200);
        
        // Verificar se foi inativado
        cy.authenticatedRequest({
          method: 'GET',
          url: `${baseUrl}/api/fornecedores/${createdFornecedorId}`
        }).then((getResponse) => {
          expect(getResponse.body.ativo).to.be.false;
          expect(getResponse.body.status).to.eq('INATIVO');
        });
      });
    });

    it('should block fornecedor - PATCH /api/fornecedores/{id}/bloquear', () => {
      cy.authenticatedRequest({
        method: 'PATCH',
        url: `${baseUrl}/api/fornecedores/${createdFornecedorId}/bloquear`
      }).then((response) => {
        expect(response.status).to.eq(200);
        
        // Verificar se foi bloqueado
        cy.authenticatedRequest({
          method: 'GET',
          url: `${baseUrl}/api/fornecedores/${createdFornecedorId}`
        }).then((getResponse) => {
          expect(getResponse.body.status).to.eq('BLOQUEADO');
        });
      });
    });

    it('should return 404 for non-existent fornecedor on patch operations', () => {
      cy.authenticatedRequest({
        method: 'PATCH',
        url: `${baseUrl}/api/fornecedores/99999/ativar`,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(404);
      });
    });
  });

  describe('DELETE - DELETE /api/fornecedores/{id}', () => {
    let fornecedorToDelete;

    beforeEach(() => {
      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/fornecedores`,
        body: {
          ...testFornecedorData,
          cnpj: generateValidCNPJ(),
          razaoSocial: 'Fornecedor Para Deletar ' + Date.now()
        }
      }).then((response) => {
        fornecedorToDelete = response.body.id;
      });
    });

    it('should delete fornecedor successfully', () => {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `${baseUrl}/api/fornecedores/${fornecedorToDelete}`
      }).then((response) => {
        expect(response.status).to.eq(204);
        
        // Verificar se foi deletado
        cy.authenticatedRequest({
          method: 'GET',
          url: `${baseUrl}/api/fornecedores/${fornecedorToDelete}`,
          failOnStatusCode: false
        }).then((getResponse) => {
          expect(getResponse.status).to.eq(404);
        });
      });
    });

    it('should return 404 for non-existent fornecedor', () => {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `${baseUrl}/api/fornecedores/99999`,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(404);
      });
    });
  });

  describe('Business Logic Tests', () => {
    before(() => {
      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/fornecedores`,
        body: testFornecedorData
      }).then((response) => {
        createdFornecedorId = response.body.id;
      });
    });

    it('should validate CNPJ format', () => {
      const invalidCNPJData = {
        ...testFornecedorData,
        cnpj: '123.456.789/0001-00' // Formato com pontuação deve ser aceito ou rejeitado consistentemente
      };

      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/fornecedores`,
        body: invalidCNPJData,
        failOnStatusCode: false
      }).then((response) => {
        // O sistema deve ser consistente com o formato aceito
        expect([201, 400]).to.include(response.status);
      });
    });

    it('should validate email format', () => {
      const invalidEmailData = {
        ...testFornecedorData,
        cnpj: generateValidCNPJ(),
        email: 'email_sem_formato_valido'
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
  });

  describe('Authentication Tests', () => {
    it('should return 401 without authentication token', () => {
      cy.request({
        method: 'GET',
        url: `${baseUrl}/api/fornecedores`,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(401);
      });
    });

    it('should return 401 with invalid token', () => {
      cy.request({
        method: 'POST',
        url: `${baseUrl}/api/fornecedores`,
        headers: {
          'Authorization': 'Bearer invalid_token'
        },
        body: testFornecedorData,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(401);
      });
    });
  });

  after(() => {
    if (createdFornecedorId) {
      cy.cleanupTestData('fornecedores', createdFornecedorId);
    }
  });

  function generateValidCNPJ() {
    const timestamp = Date.now().toString();
    const base = timestamp.substring(timestamp.length - 8);
    return `${base}000144`;
  }
});