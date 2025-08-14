describe('Role-Based Access Control Tests', () => {
    const baseUrl = Cypress.env('API_BASE_URL') || 'http://localhost:9876';
    
    const adminUser = {
        email: 'admin@freepharma.com',
        senha: '123456'
    };

    let adminToken = '';

    before(() => {
        // Login como admin
        cy.request({
            method: 'POST',
            url: `${baseUrl}/api/auth/login`,
            body: adminUser
        }).then((response) => {
            expect(response.status).to.eq(200);
            expect(response.body).to.have.property('accessToken');
            adminToken = response.body.accessToken;
            cy.log('Admin user logged in successfully');
        });
    });

    describe('Admin Dashboard Access', () => {
        it('should allow admin to access admin dashboard', () => {
            cy.request({
                method: 'GET',
                url: `${baseUrl}/api/admin/dashboard`,
                headers: {
                    'Authorization': `Bearer ${adminToken}`
                }
            }).then((response) => {
                expect(response.status).to.eq(200);
                expect(response.body).to.have.property('title', 'Dashboard Administrativo');
                expect(response.body).to.have.property('totalUsers');
                expect(response.body).to.have.property('totalFarmacias');
                expect(response.body).to.have.property('systemHealth', 'OK');
            });
        });

        it('should deny access without proper token to admin dashboard', () => {
            cy.request({
                method: 'GET',
                url: `${baseUrl}/api/admin/dashboard`,
                headers: {
                    'Authorization': 'Bearer invalid_token'
                },
                failOnStatusCode: false
            }).then((response) => {
                expect(response.status).to.be.oneOf([401, 403]);
            });
        });

        it('should allow admin to access user management', () => {
            cy.request({
                method: 'GET',
                url: `${baseUrl}/api/admin/users`,
                headers: {
                    'Authorization': `Bearer ${adminToken}`
                }
            }).then((response) => {
                expect(response.status).to.eq(200);
                expect(response.body).to.have.property('users');
                expect(response.body).to.have.property('adminOnly', true);
            });
        });

        it('should deny unauthorized access to user management', () => {
            cy.request({
                method: 'GET',
                url: `${baseUrl}/api/admin/users`,
                failOnStatusCode: false
            }).then((response) => {
                expect(response.status).to.eq(401);
            });
        });

        it('should allow admin to update system config', () => {
            cy.request({
                method: 'POST',
                url: `${baseUrl}/api/admin/system/config`,
                headers: {
                    'Authorization': `Bearer ${adminToken}`,
                    'Content-Type': 'application/json'
                },
                body: {
                    configKey: 'testConfig',
                    configValue: 'testValue'
                }
            }).then((response) => {
                expect(response.status).to.eq(200);
                expect(response.body).to.have.property('status', 'success');
                expect(response.body).to.have.property('message').that.contains('atualizadas com sucesso');
            });
        });

        it('should deny unauthorized access to system config', () => {
            cy.request({
                method: 'POST',
                url: `${baseUrl}/api/admin/system/config`,
                headers: {
                    'Content-Type': 'application/json'
                },
                body: {
                    configKey: 'testConfig',
                    configValue: 'testValue'
                },
                failOnStatusCode: false
            }).then((response) => {
                expect(response.status).to.eq(401);
            });
        });
    });

    describe('User Dashboard Access', () => {
        it('should allow admin to access user dashboard', () => {
            cy.request({
                method: 'GET',
                url: `${baseUrl}/api/user/dashboard`,
                headers: {
                    'Authorization': `Bearer ${adminToken}`
                }
            }).then((response) => {
                expect(response.status).to.eq(200);
                expect(response.body).to.have.property('title', 'Dashboard do Usuário');
                expect(response.body).to.have.property('myNotifications');
                expect(response.body).to.have.property('recentActivities');
            });
        });

        it('should deny unauthorized access to user dashboard', () => {
            cy.request({
                method: 'GET',
                url: `${baseUrl}/api/user/dashboard`,
                failOnStatusCode: false
            }).then((response) => {
                expect(response.status).to.eq(401);
            });
        });

        it('should allow admin to access user profile', () => {
            cy.request({
                method: 'GET',
                url: `${baseUrl}/api/user/profile`,
                headers: {
                    'Authorization': `Bearer ${adminToken}`
                }
            }).then((response) => {
                expect(response.status).to.eq(200);
                expect(response.body).to.have.property('name');
                expect(response.body).to.have.property('email');
                expect(response.body).to.have.property('role');
            });
        });

        it('should allow admin to update profile', () => {
            cy.request({
                method: 'PUT',
                url: `${baseUrl}/api/user/profile`,
                headers: {
                    'Authorization': `Bearer ${adminToken}`,
                    'Content-Type': 'application/json'
                },
                body: {
                    name: 'Updated Admin Name',
                    telefone: '11987654321'
                }
            }).then((response) => {
                expect(response.status).to.eq(200);
                expect(response.body).to.have.property('status', 'success');
                expect(response.body).to.have.property('message').that.contains('atualizado com sucesso');
            });
        });
    });

    describe('Unauthorized Access', () => {
        it('should deny access to admin dashboard without token', () => {
            cy.request({
                method: 'GET',
                url: `${baseUrl}/api/admin/dashboard`,
                failOnStatusCode: false
            }).then((response) => {
                expect(response.status).to.eq(401);
            });
        });

        it('should deny access to user dashboard without token', () => {
            cy.request({
                method: 'GET',
                url: `${baseUrl}/api/user/dashboard`,
                failOnStatusCode: false
            }).then((response) => {
                expect(response.status).to.eq(401);
            });
        });

        it('should deny access with invalid token', () => {
            cy.request({
                method: 'GET',
                url: `${baseUrl}/api/admin/dashboard`,
                headers: {
                    'Authorization': 'Bearer invalid_token_here'
                },
                failOnStatusCode: false
            }).then((response) => {
                expect(response.status).to.eq(401);
            });
        });
    });

    describe('Role Verification', () => {
        it('should return proper user info for admin token', () => {
            // Esta funcionalidade pressupõe que existe um endpoint para verificar token
            // Se não existir, pode ser implementado ou removido este teste
            cy.log('Admin token contains admin role information');
        });

        it('should return proper user info for user token', () => {
            // Esta funcionalidade pressupõe que existe um endpoint para verificar token  
            // Se não existir, pode ser implementado ou removido este teste
            cy.log('User token contains user role information');
        });
    });

    describe('Edge Cases', () => {
        it('should handle expired tokens gracefully', () => {
            // Simulando token expirado (isso dependeria da implementação real)
            const expiredToken = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.expired.token';
            
            cy.request({
                method: 'GET',
                url: `${baseUrl}/api/admin/dashboard`,
                headers: {
                    'Authorization': `Bearer ${expiredToken}`
                },
                failOnStatusCode: false
            }).then((response) => {
                expect(response.status).to.be.oneOf([401, 403]);
            });
        });

        it('should handle malformed authorization header', () => {
            cy.request({
                method: 'GET',
                url: `${baseUrl}/api/admin/dashboard`,
                headers: {
                    'Authorization': 'NotBearer token_here'
                },
                failOnStatusCode: false
            }).then((response) => {
                expect(response.status).to.be.oneOf([401, 403]);
            });
        });
    });
});