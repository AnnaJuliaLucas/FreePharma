describe('Authentication API Tests', () => {
  const baseUrl = Cypress.env('baseUrl') || 'http://localhost:9876';
  
  describe('POST /api/auth/login', () => {
    it('should authenticate user with valid credentials', () => {
      const loginData = {
        email: 'admin@freepharma.com',
        senha: '123456'
      };

      cy.request({
        method: 'POST',
        url: `${baseUrl}/api/auth/login`,
        body: loginData
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.have.property('accessToken');
        expect(response.body).to.have.property('refreshToken');
        expect(response.body).to.have.property('usuario');
        
        // Validate token structure
        expect(response.body.accessToken).to.be.a('string');
        expect(response.body.accessToken).to.not.be.empty;
        expect(response.body.refreshToken).to.be.a('string');
        expect(response.body.refreshToken).to.not.be.empty;
        
        // Validate user data
        expect(response.body.usuario).to.have.property('id');
        expect(response.body.usuario).to.have.property('login');
        expect(response.body.usuario).to.have.property('nome');
        
        // Store token for other tests
        Cypress.env('accessToken', response.body.accessToken);
        Cypress.env('refreshToken', response.body.refreshToken);
      });
    });

    it('should return 400 for invalid credentials', () => {
      const invalidLoginData = {
        email: 'invalid@test.com',
        senha: 'wrongpassword'
      };

      cy.request({
        method: 'POST',
        url: `${baseUrl}/api/auth/login`,
        body: invalidLoginData,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
        expect(response.body).to.have.property('error');
      });
    });

    it('should return 400 for missing email', () => {
      const incompleteData = {
        senha: 'admin123'
      };

      cy.request({
        method: 'POST',
        url: `${baseUrl}/api/auth/login`,
        body: incompleteData,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
        expect(response.body).to.have.property('error');
      });
    });

    it('should return 400 for missing password', () => {
      const incompleteData = {
        email: 'admin@freepharma.com'
      };

      cy.request({
        method: 'POST',
        url: `${baseUrl}/api/auth/login`,
        body: incompleteData,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
        expect(response.body).to.have.property('error');
      });
    });

    it('should return 400 for empty body', () => {
      cy.request({
        method: 'POST',
        url: `${baseUrl}/api/auth/login`,
        body: {},
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
        expect(response.body).to.have.property('error');
      });
    });
  });

  describe('POST /api/auth/refresh-token', () => {
    let refreshToken;

    before(() => {
      // Get a valid refresh token first
      cy.request({
        method: 'POST',
        url: `${baseUrl}/api/auth/login`,
        body: {
          email: 'admin@freepharma.com',
          senha: '123456'
        }
      }).then((response) => {
        refreshToken = response.body.refreshToken;
      });
    });

    it('should refresh token with valid refresh token', () => {
      cy.request({
        method: 'POST',
        url: `${baseUrl}/api/auth/refresh-token`,
        body: {
          refreshToken: refreshToken
        }
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.have.property('accessToken');
        expect(response.body).to.have.property('refreshToken');
        expect(response.body.accessToken).to.be.a('string');
        expect(response.body.refreshToken).to.be.a('string');
      });
    });

    it('should return 400 for invalid refresh token', () => {
      cy.request({
        method: 'POST',
        url: `${baseUrl}/api/auth/refresh-token`,
        body: {
          refreshToken: 'invalid-refresh-token'
        },
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
        expect(response.body).to.have.property('error');
      });
    });

    it('should return 400 for missing refresh token', () => {
      cy.request({
        method: 'POST',
        url: `${baseUrl}/api/auth/refresh-token`,
        body: {},
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
        expect(response.body).to.have.property('error');
        expect(response.body.error).to.eq('Refresh token é obrigatório');
      });
    });
  });

  describe('GET /api/auth/validate', () => {
    it('should validate token with valid JWT', () => {
      cy.login().then(() => {
        cy.authenticatedRequest({
          method: 'GET',
          url: `${baseUrl}/api/auth/validate`
        }).then((response) => {
          expect(response.status).to.eq(200);
          expect(response.body).to.have.property('valid', true);
          expect(response.body).to.have.property('message', 'Token válido');
        });
      });
    });

    it('should return 200 even without authorization header (public endpoint)', () => {
      cy.request({
        method: 'GET',
        url: `${baseUrl}/api/auth/validate`,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.have.property('valid', true);
      });
    });

    it('should return 200 even with invalid token (public endpoint)', () => {
      cy.request({
        method: 'GET',
        url: `${baseUrl}/api/auth/validate`,
        headers: {
          'Authorization': 'Bearer invalid-token'
        },
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.have.property('valid', true);
      });
    });
  });

  describe('POST /api/auth/logout', () => {
    it('should logout successfully', () => {
      cy.login().then(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: `${baseUrl}/api/auth/logout`
        }).then((response) => {
          expect(response.status).to.eq(200);
          expect(response.body).to.have.property('message', 'Logout realizado com sucesso');
        });
      });
    });

    it('should logout even without valid token', () => {
      cy.request({
        method: 'POST',
        url: `${baseUrl}/api/auth/logout`
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.have.property('message', 'Logout realizado com sucesso');
      });
    });
  });
});