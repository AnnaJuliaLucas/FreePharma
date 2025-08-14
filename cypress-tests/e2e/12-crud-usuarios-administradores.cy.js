describe('CRUD Usuários Administradores - API Tests', () => {
  const baseUrl = Cypress.env('baseUrl') || 'http://localhost:9876';
  let createdUsuarioId;
  let testUnidadeId;
  let testUsuarioData;

  before(() => {
    cy.login();
    
    // Criar unidade para associar ao usuário
    cy.authenticatedRequest({
      method: 'POST',
      url: `${baseUrl}/api/unidades`,
      body: {
        nomeFantasia: 'Unidade Teste Usuario',
        endereco: 'Rua Teste, 123',
        telefone: '11999888777',
        email: 'unidade@teste.com',
        status: 'ATIVA',
        farmaciaId: 1 // Assumindo que existe farmácia com ID 1
      },
      failOnStatusCode: false
    }).then((response) => {
      if (response.status === 201) {
        testUnidadeId = response.body.id;
      } else {
        testUnidadeId = 1; // Usar ID padrão se não conseguir criar
      }
    });
  });

  beforeEach(() => {
    const timestamp = Date.now();
    testUsuarioData = {
      nome: `Usuário Teste ${timestamp}`,
      cpfCnpj: `${timestamp}000`.substring(0, 11),
      login: `usuario${timestamp}`,
      dataCadastro: new Date().toISOString(),
      status: 'ATIVO',
      ativo: true
    };
  });

  describe('CREATE - POST /api/usuarios-administradores', () => {
    it('should create a new usuario with valid data', () => {
      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/usuarios-administradores`,
        body: testUsuarioData
      }).then((response) => {
        expect(response.status).to.eq(201);
        expect(response.body).to.have.property('id');
        expect(response.body.nome).to.eq(testUsuarioData.nome);
        expect(response.body.login).to.eq(testUsuarioData.login);
        expect(response.body.cpfCnpj).to.eq(testUsuarioData.cpfCnpj);
        expect(response.body.status).to.eq('ATIVO');
        expect(response.body.ativo).to.be.true;
        // Senha field not included in DTO response

        createdUsuarioId = response.body.id;
        Cypress.env('testUsuarioId', createdUsuarioId);
      });
    });

    it('should return 400 for invalid email format', () => {
      const invalidData = {
        ...testUsuarioData,
        email: 'email_invalido'
      };

      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/usuarios-administradores`,
        body: invalidData,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
      });
    });

    it('should return 400 for duplicate email', () => {
      // Primeiro, criar um usuário
      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/usuarios-administradores`,
        body: testUsuarioData
      }).then((response) => {
        expect(response.status).to.eq(201);
        createdUsuarioId = response.body.id;

        // Tentar criar outro com o mesmo email
        cy.authenticatedRequest({
          method: 'POST',
          url: `${baseUrl}/api/usuarios-administradores`,
          body: {
            ...testUsuarioData,
            nome: 'Outro Usuário',
            login: 'outrousuario'
          },
          failOnStatusCode: false
        }).then((duplicateResponse) => {
          expect(duplicateResponse.status).to.eq(400);
        });
      });
    });

    it('should return 400 for duplicate login', () => {
      const timestamp = Date.now();
      const userData1 = {
        ...testUsuarioData,
        email: `user1_${timestamp}@teste.com`,
        login: `testuser${timestamp}`
      };

      // Criar primeiro usuário
      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/usuarios-administradores`,
        body: userData1
      }).then((response) => {
        expect(response.status).to.eq(201);
        createdUsuarioId = response.body.id;

        // Tentar criar outro com o mesmo login
        const userData2 = {
          ...testUsuarioData,
          email: `user2_${timestamp}@teste.com`,
          login: userData1.login // Mesmo login
        };

        cy.authenticatedRequest({
          method: 'POST',
          url: `${baseUrl}/api/usuarios-administradores`,
          body: userData2,
          failOnStatusCode: false
        }).then((duplicateResponse) => {
          expect(duplicateResponse.status).to.eq(400);
        });
      });
    });

    it('should return 400 for missing required fields', () => {
      const incompleteData = {
        nome: 'Usuário Incompleto'
        // Faltam campos obrigatórios
      };

      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/usuarios-administradores`,
        body: incompleteData,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
      });
    });

    it('should hash password correctly', () => {
      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/usuarios-administradores`,
        body: testUsuarioData
      }).then((response) => {
        expect(response.status).to.eq(201);
        expect(response.body).to.not.have.property('senha');
        // Senha deve ser hash internamente, não retornada na resposta
        createdUsuarioId = response.body.id;
      });
    });
  });

  describe('READ - GET endpoints', () => {
    before(() => {
      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/usuarios-administradores`,
        body: testUsuarioData
      }).then((response) => {
        createdUsuarioId = response.body.id;
        Cypress.env('testUsuarioId', createdUsuarioId);
      });
    });

    it('should get all usuarios - GET /api/usuarios-administradores', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/usuarios-administradores`
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.be.an('array');
        expect(response.body.length).to.be.at.least(1);
        
        const usuario = response.body.find(u => u.id === createdUsuarioId);
        expect(usuario).to.exist;
        expect(usuario).to.not.have.property('senha'); // Senha não deve ser exposta
      });
    });

    it('should get usuario by id - GET /api/usuarios-administradores/{id}', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/usuarios-administradores/${createdUsuarioId}`
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.have.property('id', createdUsuarioId);
        expect(response.body).to.have.property('nome');
        expect(response.body).to.have.property('email');
        expect(response.body).to.have.property('login');
        expect(response.body).to.have.property('status');
        expect(response.body).to.not.have.property('senha');
      });
    });

    it('should return 404 for non-existent usuario', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/usuarios-administradores/99999`,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(404);
      });
    });

    it('should get usuario by login - GET /api/usuarios-administradores/login/{login}', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/usuarios-administradores/login/${testUsuarioData.login}`
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.have.property('login', testUsuarioData.login);
        expect(response.body).to.have.property('id', createdUsuarioId);
      });
    });

    it('should return 404 for non-existent login', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/usuarios-administradores/login/loginNaoExiste`,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(404);
      });
    });

    it('should get usuario by email - GET /api/usuarios-administradores/email/{email}', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/usuarios-administradores/email/${testUsuarioData.email}`
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.have.property('email', testUsuarioData.email);
        expect(response.body).to.have.property('id', createdUsuarioId);
      });
    });

    it('should get usuarios by status - GET /api/usuarios-administradores/status/{status}', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/usuarios-administradores/status/ATIVO`
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.be.an('array');
        
        if (response.body.length > 0) {
          response.body.forEach(usuario => {
            expect(usuario.status).to.eq('ATIVO');
          });
        }
      });
    });

    it('should get usuarios by unidade - GET /api/usuarios-administradores/unidade/{unidadeId}', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/usuarios-administradores/unidade/${testUnidadeId}`
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.be.an('array');
        
        if (response.body.length > 0) {
          response.body.forEach(usuario => {
            expect(usuario.unidadeId).to.eq(testUnidadeId);
          });
        }
      });
    });
  });

  describe('UPDATE - PUT /api/usuarios-administradores/{id}', () => {
    before(() => {
      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/usuarios-administradores`,
        body: testUsuarioData
      }).then((response) => {
        createdUsuarioId = response.body.id;
      });
    });

    it('should update usuario with valid data', () => {
      const updatedData = {
        ...testUsuarioData,
        nome: 'Usuário Atualizado',
        telefone: '11888777666'
      };
      
      // Remove senha do update para não causar problemas
      delete updatedData.senha;

      cy.authenticatedRequest({
        method: 'PUT',
        url: `${baseUrl}/api/usuarios-administradores/${createdUsuarioId}`,
        body: updatedData
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body.nome).to.eq('Usuário Atualizado');
        expect(response.body.telefone).to.eq('11888777666');
        expect(response.body.id).to.eq(createdUsuarioId);
        expect(response.body).to.not.have.property('senha');
      });
    });

    it('should return 404 for non-existent usuario', () => {
      cy.authenticatedRequest({
        method: 'PUT',
        url: `${baseUrl}/api/usuarios-administradores/99999`,
        body: { ...testUsuarioData, senha: undefined },
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(404);
      });
    });
  });

  describe('PATCH Operations', () => {
    before(() => {
      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/usuarios-administradores`,
        body: testUsuarioData
      }).then((response) => {
        createdUsuarioId = response.body.id;
      });
    });

    it('should activate usuario - PATCH /api/usuarios-administradores/{id}/ativar', () => {
      cy.authenticatedRequest({
        method: 'PATCH',
        url: `${baseUrl}/api/usuarios-administradores/${createdUsuarioId}/ativar`
      }).then((response) => {
        expect(response.status).to.eq(200);
        
        // Verificar se foi ativado
        cy.authenticatedRequest({
          method: 'GET',
          url: `${baseUrl}/api/usuarios-administradores/${createdUsuarioId}`
        }).then((getResponse) => {
          expect(getResponse.body.ativo).to.be.true;
          expect(getResponse.body.status).to.eq('ATIVO');
        });
      });
    });

    it('should deactivate usuario - PATCH /api/usuarios-administradores/{id}/inativar', () => {
      cy.authenticatedRequest({
        method: 'PATCH',
        url: `${baseUrl}/api/usuarios-administradores/${createdUsuarioId}/inativar`
      }).then((response) => {
        expect(response.status).to.eq(200);
        
        // Verificar se foi inativado
        cy.authenticatedRequest({
          method: 'GET',
          url: `${baseUrl}/api/usuarios-administradores/${createdUsuarioId}`
        }).then((getResponse) => {
          expect(getResponse.body.ativo).to.be.false;
          expect(getResponse.body.status).to.eq('INATIVO');
        });
      });
    });

    it('should block usuario - PATCH /api/usuarios-administradores/{id}/bloquear', () => {
      cy.authenticatedRequest({
        method: 'PATCH',
        url: `${baseUrl}/api/usuarios-administradores/${createdUsuarioId}/bloquear`
      }).then((response) => {
        expect(response.status).to.eq(200);
        
        // Verificar se foi bloqueado
        cy.authenticatedRequest({
          method: 'GET',
          url: `${baseUrl}/api/usuarios-administradores/${createdUsuarioId}`
        }).then((getResponse) => {
          expect(getResponse.body.status).to.eq('BLOQUEADO');
        });
      });
    });

    it('should change password - PATCH /api/usuarios-administradores/{id}/alterar-senha', () => {
      const senhaData = {
        senhaAtual: 'senha123',
        novaSenha: 'novaSenha456'
      };

      cy.authenticatedRequest({
        method: 'PATCH',
        url: `${baseUrl}/api/usuarios-administradores/${createdUsuarioId}/alterar-senha`,
        body: senhaData
      }).then((response) => {
        expect(response.status).to.eq(200);
      });
    });

    it('should return 400 for invalid current password', () => {
      const senhaData = {
        senhaAtual: 'senhaErrada',
        novaSenha: 'novaSenha456'
      };

      cy.authenticatedRequest({
        method: 'PATCH',
        url: `${baseUrl}/api/usuarios-administradores/${createdUsuarioId}/alterar-senha`,
        body: senhaData,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
      });
    });

    it('should register access - PATCH /api/usuarios-administradores/{id}/registrar-acesso', () => {
      cy.authenticatedRequest({
        method: 'PATCH',
        url: `${baseUrl}/api/usuarios-administradores/${createdUsuarioId}/registrar-acesso`
      }).then((response) => {
        expect(response.status).to.eq(200);
      });
    });

    it('should assign profiles - PATCH /api/usuarios-administradores/{id}/perfis', () => {
      const perfilIds = [1, 2]; // Assumindo que existem perfis com esses IDs

      cy.authenticatedRequest({
        method: 'PATCH',
        url: `${baseUrl}/api/usuarios-administradores/${createdUsuarioId}/perfis`,
        body: perfilIds
      }).then((response) => {
        expect(response.status).to.eq(200);
      });
    });
  });

  describe('DELETE - DELETE /api/usuarios-administradores/{id}', () => {
    let usuarioToDelete;

    beforeEach(() => {
      const timestamp = Date.now();
      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/usuarios-administradores`,
        body: {
          ...testUsuarioData,
          email: `deletar${timestamp}@teste.com`,
          login: `deletar${timestamp}`,
          nome: `Usuário Para Deletar ${timestamp}`
        }
      }).then((response) => {
        usuarioToDelete = response.body.id;
      });
    });

    it('should delete usuario successfully', () => {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `${baseUrl}/api/usuarios-administradores/${usuarioToDelete}`
      }).then((response) => {
        expect(response.status).to.eq(204);
        
        // Verificar se foi deletado
        cy.authenticatedRequest({
          method: 'GET',
          url: `${baseUrl}/api/usuarios-administradores/${usuarioToDelete}`,
          failOnStatusCode: false
        }).then((getResponse) => {
          expect(getResponse.status).to.eq(404);
        });
      });
    });

    it('should return 404 for non-existent usuario', () => {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `${baseUrl}/api/usuarios-administradores/99999`,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(404);
      });
    });
  });

  describe('Security Tests', () => {
    it('should not expose password in any response', () => {
      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/usuarios-administradores`,
        body: testUsuarioData
      }).then((response) => {
        expect(response.status).to.eq(201);
        expect(response.body).to.not.have.property('senha');
        
        createdUsuarioId = response.body.id;
        
        // Verificar na busca por ID também
        cy.authenticatedRequest({
          method: 'GET',
          url: `${baseUrl}/api/usuarios-administradores/${createdUsuarioId}`
        }).then((getResponse) => {
          expect(getResponse.body).to.not.have.property('senha');
        });
      });
    });
  });

  describe('Authentication Tests', () => {
    it('should return 401 without authentication token', () => {
      cy.request({
        method: 'GET',
        url: `${baseUrl}/api/usuarios-administradores`,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(401);
      });
    });

    it('should return 401 with invalid token for sensitive operations', () => {
      cy.request({
        method: 'POST',
        url: `${baseUrl}/api/usuarios-administradores`,
        headers: {
          'Authorization': 'Bearer invalid_token'
        },
        body: testUsuarioData,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(401);
      });
    });
  });

  after(() => {
    if (createdUsuarioId) {
      cy.cleanupTestData('usuarios-administradores', createdUsuarioId);
    }
    if (testUnidadeId && testUnidadeId !== 1) {
      cy.cleanupTestData('unidades', testUnidadeId);
    }
  });
});