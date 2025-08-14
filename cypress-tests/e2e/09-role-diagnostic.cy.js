describe('Diagnostic Tests for Role-Based Access', () => {
    const baseUrl = Cypress.env('API_BASE_URL') || 'http://localhost:9876';
    
    const adminUser = {
        email: 'admin@freepharma.com',
        senha: '123456'
    };

    let adminToken = '';

    it('should successfully login and get token', () => {
        cy.request({
            method: 'POST',
            url: `${baseUrl}/api/auth/login`,
            body: adminUser
        }).then((response) => {
            expect(response.status).to.eq(200);
            expect(response.body).to.have.property('accessToken');
            adminToken = response.body.accessToken;
            
            cy.log('Token received:', adminToken);
            cy.log('User data:', JSON.stringify(response.body.usuario));
        });
    });

    it('should access existing user dashboard endpoint', () => {
        cy.request({
            method: 'GET',
            url: `${baseUrl}/api/user/dashboard`,
            headers: {
                'Authorization': `Bearer ${adminToken}`
            },
            failOnStatusCode: false
        }).then((response) => {
            cy.log('Response status:', response.status);
            cy.log('Response body:', JSON.stringify(response.body));
        });
    });

    it('should access admin dashboard endpoint', () => {
        cy.request({
            method: 'GET',
            url: `${baseUrl}/api/admin/dashboard`,
            headers: {
                'Authorization': `Bearer ${adminToken}`
            },
            failOnStatusCode: false
        }).then((response) => {
            cy.log('Response status:', response.status);
            cy.log('Response body:', JSON.stringify(response.body));
        });
    });

    it('should check token validity', () => {
        // Tenta acessar um endpoint protegido já existente para verificar se o token funciona
        cy.request({
            method: 'GET',
            url: `${baseUrl}/api/farmacias`,
            headers: {
                'Authorization': `Bearer ${adminToken}`
            },
            failOnStatusCode: false
        }).then((response) => {
            cy.log('Farmacias endpoint status:', response.status);
            cy.log('Farmacias response:', JSON.stringify(response.body));
        });
    });

    it('should test unauthorized access', () => {
        cy.request({
            method: 'GET',
            url: `${baseUrl}/api/admin/dashboard`,
            failOnStatusCode: false
        }).then((response) => {
            cy.log('Unauthorized access status:', response.status);
            expect(response.status).to.eq(401);
        });
    });
});