describe('Usuario Administrador API Tests', () => {
  const baseUrl = Cypress.env('baseUrl') || 'http://localhost:9876';
  let createdUsuarioId;
  let unidadeId;
  let farmaciaId;
  
  before(() => {
    cy.login();
    // Create test farmacia and unidade first
    cy.createTestFarmacia().then((farmacia) => {
      farmaciaId = farmacia.id;
      
      // Create unidade for the farmacia
      const unidadeData = {
        nomeFantasia: 'Unidade Teste Cypress',
        endereco: window.MockDataGenerator.generateAddress(),
        telefone: window.MockDataGenerator.generatePhone(),
        email: window.MockDataGenerator.generateEmail('unidade'),
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
    if (createdUsuarioId) {
      cy.cleanupTestData('usuarios-administradores', createdUsuarioId);
    }
    if (unidadeId) {
      cy.cleanupTestData('unidades', unidadeId);
    }
    if (farmaciaId) {
      cy.cleanupTestData('farmacias', farmaciaId);
    }
  });

  describe('GET /api/usuarios-administradores', () => {
    it('should list all usuarios with authentication', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/usuarios-administradores`
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.be.an('array');
        
        if (response.body.length > 0) {
          const usuario = response.body[0];
          expect(usuario).to.have.property('id');
          expect(usuario).to.have.property('login');
          expect(usuario).to.not.have.property('senha'); // Senha não deve ser retornada
        }
      });
    });

    it('should return 401 without authentication', () => {
      cy.request({
        method: 'GET',
        url: `${baseUrl}/api/usuarios-administradores`,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(401);
      });
    });
  });

  describe('POST /api/usuarios-administradores', () => {
    it('should create new usuario with valid data', () => {
      const usuarioData = window.MockDataGenerator.generateUsuarioData(unidadeId);

      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/usuarios-administradores`,
        body: usuarioData
      }).then((response) => {
        expect(response.status).to.eq(201);
        expect(response.body).to.have.property('id');
        expect(response.body.login).to.eq(usuarioData.login);
        expect(response.body.nome).to.eq(usuarioData.nome);
        expect(response.body.email).to.eq(usuarioData.email);
        expect(response.body.status).to.eq('ATIVO');
        expect(response.body).to.not.have.property('senha'); // Senha não deve ser retornada
        
        createdUsuarioId = response.body.id;
      });
    });

    it('should return 400 for invalid login (too short)', () => {
      const invalidData = {
        login: 'ab', // Muito curto
        nome: 'Usuario Teste',
        senha: 'senha123'
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

    it('should return 400 for missing required fields', () => {
      const incompleteData = {
        login: 'usuario.incompleto'
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

    it('should return 400 for invalid email format', () => {
      const invalidEmailData = {
        login: 'usuario.email.invalido',
        nome: 'Usuario Email Invalido',
        email: 'email-invalido',
        senha: 'senha123'
      };

      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/usuarios-administradores`,
        body: invalidEmailData,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
      });
    });

    it('should return 400 for duplicate login', () => {
      const duplicateData = {
        login: 'usuario.cypress', // Mesmo login do usuário criado anteriormente
        nome: 'Outro Usuario',
        senha: 'outrasenha123'
      };

      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/usuarios-administradores`,
        body: duplicateData,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
      });
    });

    it('should return 400 for duplicate email', () => {
      const duplicateEmailData = {
        login: 'usuario.outro',
        nome: 'Outro Usuario',
        email: 'usuario.cypress@teste.com', // Mesmo email
        senha: 'outrasenha123'
      };

      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/usuarios-administradores`,
        body: duplicateEmailData,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
      });
    });

    it('should return 400 for non-existent unidade', () => {
      const invalidUnidadeData = {
        login: 'usuario.unidade.invalida',
        nome: 'Usuario Unidade Invalida',
        senha: 'senha123',
        unidadesAcesso: [{
          id: 999999
        }]
      };

      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/usuarios-administradores`,
        body: invalidUnidadeData,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
      });
    });
  });

  describe('GET /api/usuarios-administradores/{id}', () => {
    it('should get usuario by valid ID', () => {
      if (createdUsuarioId) {
        cy.authenticatedRequest({
          method: 'GET',
          url: `${baseUrl}/api/usuarios-administradores/${createdUsuarioId}`
        }).then((response) => {
          expect(response.status).to.eq(200);
          expect(response.body).to.have.property('id', createdUsuarioId);
          expect(response.body).to.have.property('login');
          expect(response.body).to.have.property('nome');
          expect(response.body).to.not.have.property('senha');
        });
      }
    });

    it('should return 404 for non-existent ID', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/usuarios-administradores/999999`,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(404);
      });
    });
  });

  describe('GET /api/usuarios-administradores/login/{login}', () => {
    it('should get usuario by valid login', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/usuarios-administradores/login/usuario.cypress`
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.have.property('login', 'usuario.cypress');
      });
    });

    it('should return 404 for non-existent login', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/usuarios-administradores/login/usuario.inexistente`,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(404);
      });
    });
  });

  describe('GET /api/usuarios-administradores/email/{email}', () => {
    it('should get usuario by valid email', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/usuarios-administradores/email/usuario.cypress@teste.com`
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.have.property('email', 'usuario.cypress@teste.com');
      });
    });

    it('should return 404 for non-existent email', () => {
      cy.authenticatedRequest({
        method: 'GET',
        url: `${baseUrl}/api/usuarios-administradores/email/inexistente@teste.com`,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(404);
      });
    });
  });

  describe('GET /api/usuarios-administradores/status/{status}', () => {
    it('should get usuarios by status', () => {
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
  });

  describe('PUT /api/usuarios-administradores/{id}', () => {
    it('should update usuario with valid data', () => {
      if (createdUsuarioId) {
        const updateData = {
          login: 'usuario.cypress', // Login não pode ser alterado
          nome: 'Usuario Cypress Teste - Atualizado',
          email: 'usuario.cypress.updated@teste.com',
          telefone: '11999888777',
          cargo: 'Administrador Senior',
          status: 'ATIVO'
        };

        cy.authenticatedRequest({
          method: 'PUT',
          url: `${baseUrl}/api/usuarios-administradores/${createdUsuarioId}`,
          body: updateData
        }).then((response) => {
          expect(response.status).to.eq(200);
          expect(response.body.nome).to.eq(updateData.nome);
          expect(response.body.telefone).to.eq(updateData.telefone);
          expect(response.body.cargo).to.eq(updateData.cargo);
          expect(response.body.login).to.eq('usuario.cypress'); // Login mantido
        });
      }
    });

    it('should return 400 for non-existent ID', () => {
      const updateData = {
        nome: 'Usuario Inexistente',
        login: 'inexistente'
      };

      cy.authenticatedRequest({
        method: 'PUT',
        url: `${baseUrl}/api/usuarios-administradores/999999`,
        body: updateData,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
      });
    });
  });

  describe('PATCH /api/usuarios-administradores/{id}/alterar-senha', () => {
    it('should change password with valid data', () => {
      if (createdUsuarioId) {
        const senhaData = {
          senhaAtual: 'senha123456',
          novaSenha: 'novasenha123456'
        };

        cy.authenticatedRequest({
          method: 'PATCH',
          url: `${baseUrl}/api/usuarios-administradores/${createdUsuarioId}/alterar-senha`,
          body: senhaData
        }).then((response) => {
          expect(response.status).to.eq(200);
        });
      }
    });

    it('should return 400 for invalid current password', () => {
      if (createdUsuarioId) {
        const invalidSenhaData = {
          senhaAtual: 'senhaerrada',
          novaSenha: 'novasenha123456'
        };

        cy.authenticatedRequest({
          method: 'PATCH',
          url: `${baseUrl}/api/usuarios-administradores/${createdUsuarioId}/alterar-senha`,
          body: invalidSenhaData,
          failOnStatusCode: false
        }).then((response) => {
          expect(response.status).to.eq(400);
        });
      }
    });

    it('should return 400 for short new password', () => {
      if (createdUsuarioId) {
        const shortPasswordData = {
          senhaAtual: 'novasenha123456',
          novaSenha: '123' // Muito curta
        };

        cy.authenticatedRequest({
          method: 'PATCH',
          url: `${baseUrl}/api/usuarios-administradores/${createdUsuarioId}/alterar-senha`,
          body: shortPasswordData,
          failOnStatusCode: false
        }).then((response) => {
          expect(response.status).to.eq(400);
        });
      }
    });
  });

  describe('PATCH /api/usuarios-administradores/{id}/ativar', () => {
    it('should activate usuario', () => {
      if (createdUsuarioId) {
        cy.authenticatedRequest({
          method: 'PATCH',
          url: `${baseUrl}/api/usuarios-administradores/${createdUsuarioId}/ativar`
        }).then((response) => {
          expect(response.status).to.eq(200);
        });
      }
    });
  });

  describe('PATCH /api/usuarios-administradores/{id}/inativar', () => {
    it('should inactivate usuario', () => {
      if (createdUsuarioId) {
        cy.authenticatedRequest({
          method: 'PATCH',
          url: `${baseUrl}/api/usuarios-administradores/${createdUsuarioId}/inativar`
        }).then((response) => {
          expect(response.status).to.eq(200);
        });
      }
    });
  });

  describe('PATCH /api/usuarios-administradores/{id}/bloquear', () => {
    it('should block usuario', () => {
      if (createdUsuarioId) {
        cy.authenticatedRequest({
          method: 'PATCH',
          url: `${baseUrl}/api/usuarios-administradores/${createdUsuarioId}/bloquear`
        }).then((response) => {
          expect(response.status).to.eq(200);
        });
      }
    });
  });

  describe('PATCH /api/usuarios-administradores/{id}/perfis', () => {
    it('should assign perfis to usuario', () => {
      if (createdUsuarioId) {
        const perfilIds = [1, 2]; // IDs dos perfis

        cy.authenticatedRequest({
          method: 'PATCH',
          url: `${baseUrl}/api/usuarios-administradores/${createdUsuarioId}/perfis`,
          body: perfilIds
        }).then((response) => {
          expect(response.status).to.eq(200);
        });
      }
    });
  });

  describe('PATCH /api/usuarios-administradores/{id}/registrar-acesso', () => {
    it('should register user access', () => {
      if (createdUsuarioId) {
        cy.authenticatedRequest({
          method: 'PATCH',
          url: `${baseUrl}/api/usuarios-administradores/${createdUsuarioId}/registrar-acesso`
        }).then((response) => {
          expect(response.status).to.eq(200);
        });
      }
    });
  });

  describe('DELETE /api/usuarios-administradores/{id}', () => {
    it('should delete usuario', () => {
      if (createdUsuarioId) {
        cy.authenticatedRequest({
          method: 'DELETE',
          url: `${baseUrl}/api/usuarios-administradores/${createdUsuarioId}`
        }).then((response) => {
          expect(response.status).to.eq(204);
          createdUsuarioId = null; // Evita cleanup duplo
        });
      }
    });

    it('should return 400 for non-existent ID', () => {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `${baseUrl}/api/usuarios-administradores/999999`,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
      });
    });
  });
});