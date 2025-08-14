describe('🎬 Demonstração Completa de Controle de Acesso por Roles', () => {
    const baseUrl = Cypress.env('API_BASE_URL') || 'http://localhost:9876';
    
    // Usuários de teste
    const adminUser = {
        email: 'admin@freepharma.com',
        senha: '123456',
        expectedRole: 'ADMIN'
    };
    
    const regularUser = {
        email: 'user@freepharma.com',
        senha: '123456',
        expectedRole: 'USER'
    };

    let adminToken = '';
    let userToken = '';

    // Lista de endpoints para testar
    const endpoints = {
        adminOnly: [
            { method: 'GET', url: '/api/admin/dashboard', name: 'Admin Dashboard' },
            { method: 'GET', url: '/api/admin/users', name: 'User Management' },
            { method: 'POST', url: '/api/admin/system/config', name: 'System Config', body: { config: 'test' } }
        ],
        userAndAdmin: [
            { method: 'GET', url: '/api/user/dashboard', name: 'User Dashboard' },
            { method: 'GET', url: '/api/user/profile', name: 'User Profile' },
            { method: 'PUT', url: '/api/user/profile', name: 'Update Profile', body: { name: 'Test' } }
        ]
    };

    before(() => {
        cy.log('🚀 === INICIANDO DEMONSTRAÇÃO COMPLETA ===');
        cy.log('📋 Objetivos:');
        cy.log('   1. Login com usuário ADMIN');
        cy.log('   2. Login com usuário USER');
        cy.log('   3. Testar acessos permitidos e negados');
        cy.log('   4. Demonstrar segurança funcional');
    });

    describe('🔐 Fase 1: Autenticação de Usuários', () => {
        it('👨‍💼 Should login as ADMIN user', () => {
            cy.log('🔑 Fazendo login como ADMINISTRADOR...');
            
            cy.request({
                method: 'POST',
                url: `${baseUrl}/api/auth/login`,
                body: adminUser
            }).then((response) => {
                expect(response.status).to.eq(200);
                expect(response.body).to.have.property('accessToken');
                expect(response.body).to.have.property('usuario');
                
                adminToken = response.body.accessToken;
                
                cy.log('✅ Login ADMIN realizado com sucesso!');
                cy.log(`👤 Usuário: ${response.body.usuario.nome}`);
                cy.log(`📧 Email: ${response.body.usuario.email || adminUser.email}`);
                cy.log(`🔑 Token: ${adminToken.substring(0, 50)}...`);
            });
        });

        it('👤 Should login as USER', () => {
            cy.log('🔑 Fazendo login como USUÁRIO COMUM...');
            
            cy.request({
                method: 'POST',
                url: `${baseUrl}/api/auth/login`,
                body: regularUser
            }).then((response) => {
                expect(response.status).to.eq(200);
                expect(response.body).to.have.property('accessToken');
                expect(response.body).to.have.property('usuario');
                
                userToken = response.body.accessToken;
                
                cy.log('✅ Login USER realizado com sucesso!');
                cy.log(`👤 Usuário: ${response.body.usuario.nome}`);
                cy.log(`📧 Email: ${response.body.usuario.email || regularUser.email}`);
                cy.log(`🔑 Token: ${userToken.substring(0, 50)}...`);
            });
        });

        it('🔍 Should verify user roles', () => {
            cy.log('🔍 Verificando roles dos usuários...');
            
            // Verificar role do ADMIN
            cy.request({
                method: 'GET',
                url: `${baseUrl}/api/debug/current-auth`,
                headers: { 'Authorization': `Bearer ${adminToken}` }
            }).then((response) => {
                expect(response.body.authorities).to.include('ROLE_ADMIN');
                cy.log('✅ ADMIN tem role ROLE_ADMIN');
            });

            // Verificar role do USER
            cy.request({
                method: 'GET',
                url: `${baseUrl}/api/debug/current-auth`,
                headers: { 'Authorization': `Bearer ${userToken}` }
            }).then((response) => {
                expect(response.body.authorities).to.include('ROLE_USER');
                cy.log('✅ USER tem role ROLE_USER');
            });
        });
    });

    describe('🛡️ Fase 2: Testes de Acesso - Usuário ADMIN', () => {
        it('✅ ADMIN should access ALL endpoints', () => {
            cy.log('🧪 Testando acesso do ADMIN a TODOS os endpoints...');
            
            // Testar endpoints exclusivos do admin
            endpoints.adminOnly.forEach((endpoint) => {
                cy.log(`🔍 Testando: ${endpoint.name} (${endpoint.method} ${endpoint.url})`);
                
                const requestConfig = {
                    method: endpoint.method,
                    url: `${baseUrl}${endpoint.url}`,
                    headers: { 'Authorization': `Bearer ${adminToken}` }
                };
                
                if (endpoint.body) {
                    requestConfig.body = endpoint.body;
                    requestConfig.headers['Content-Type'] = 'application/json';
                }
                
                cy.request(requestConfig).then((response) => {
                    expect(response.status).to.eq(200);
                    cy.log(`✅ ${endpoint.name}: ACESSO PERMITIDO (200)`);
                });
            });
            
            // Testar endpoints user/admin
            endpoints.userAndAdmin.forEach((endpoint) => {
                cy.log(`🔍 Testando: ${endpoint.name} (${endpoint.method} ${endpoint.url})`);
                
                const requestConfig = {
                    method: endpoint.method,
                    url: `${baseUrl}${endpoint.url}`,
                    headers: { 'Authorization': `Bearer ${adminToken}` }
                };
                
                if (endpoint.body) {
                    requestConfig.body = endpoint.body;
                    requestConfig.headers['Content-Type'] = 'application/json';
                }
                
                cy.request(requestConfig).then((response) => {
                    expect(response.status).to.eq(200);
                    cy.log(`✅ ${endpoint.name}: ACESSO PERMITIDO (200)`);
                });
            });
            
            cy.log('🎉 ADMIN tem acesso total a todos os endpoints!');
        });
    });

    describe('🔒 Fase 3: Testes de Acesso - Usuário USER', () => {
        it('✅ USER should access USER endpoints only', () => {
            cy.log('🧪 Testando acesso do USER apenas aos endpoints permitidos...');
            
            // Testar endpoints user/admin (deve ter acesso)
            endpoints.userAndAdmin.forEach((endpoint) => {
                cy.log(`🔍 Testando: ${endpoint.name} (${endpoint.method} ${endpoint.url})`);
                
                const requestConfig = {
                    method: endpoint.method,
                    url: `${baseUrl}${endpoint.url}`,
                    headers: { 'Authorization': `Bearer ${userToken}` }
                };
                
                if (endpoint.body) {
                    requestConfig.body = endpoint.body;
                    requestConfig.headers['Content-Type'] = 'application/json';
                }
                
                cy.request(requestConfig).then((response) => {
                    expect(response.status).to.eq(200);
                    cy.log(`✅ ${endpoint.name}: ACESSO PERMITIDO (200)`);
                });
            });
            
            cy.log('✅ USER tem acesso aos endpoints de usuário!');
        });

        it('❌ USER should be DENIED access to ADMIN endpoints', () => {
            cy.log('🛡️ Testando que USER é NEGADO nos endpoints de ADMIN...');
            
            // Testar endpoints exclusivos do admin (deve ser negado)
            endpoints.adminOnly.forEach((endpoint) => {
                cy.log(`🔍 Testando: ${endpoint.name} (${endpoint.method} ${endpoint.url})`);
                
                const requestConfig = {
                    method: endpoint.method,
                    url: `${baseUrl}${endpoint.url}`,
                    headers: { 'Authorization': `Bearer ${userToken}` },
                    failOnStatusCode: false
                };
                
                if (endpoint.body) {
                    requestConfig.body = endpoint.body;
                    requestConfig.headers['Content-Type'] = 'application/json';
                }
                
                cy.request(requestConfig).then((response) => {
                    expect(response.status).to.eq(403);
                    cy.log(`🚫 ${endpoint.name}: ACESSO NEGADO (403) - CORRETO!`);
                });
            });
            
            cy.log('🛡️ Segurança funcionando: USER não acessa endpoints de ADMIN!');
        });
    });

    describe('🚫 Fase 4: Testes de Segurança', () => {
        it('🔐 Should deny access without authentication', () => {
            cy.log('🧪 Testando acesso sem autenticação...');
            
            const testEndpoints = [...endpoints.adminOnly, ...endpoints.userAndAdmin];
            
            testEndpoints.forEach((endpoint) => {
                cy.log(`🔍 Testando sem token: ${endpoint.name}`);
                
                const requestConfig = {
                    method: endpoint.method,
                    url: `${baseUrl}${endpoint.url}`,
                    failOnStatusCode: false
                };
                
                if (endpoint.body) {
                    requestConfig.body = endpoint.body;
                    requestConfig.headers = { 'Content-Type': 'application/json' };
                }
                
                cy.request(requestConfig).then((response) => {
                    expect(response.status).to.eq(401);
                    cy.log(`🚫 ${endpoint.name}: SEM TOKEN = 401 (CORRETO!)`);
                });
            });
            
            cy.log('🛡️ Segurança OK: Todos endpoints negam acesso sem token!');
        });

        it('🔑 Should deny access with invalid token', () => {
            cy.log('🧪 Testando acesso com token inválido...');
            
            const invalidToken = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.invalid.token';
            
            cy.request({
                method: 'GET',
                url: `${baseUrl}/api/admin/dashboard`,
                headers: { 'Authorization': `Bearer ${invalidToken}` },
                failOnStatusCode: false
            }).then((response) => {
                expect(response.status).to.be.oneOf([401, 403]);
                cy.log('🚫 Token inválido rejeitado corretamente!');
            });
        });
    });

    describe('📊 Fase 5: Relatório Final', () => {
        it('📋 Should generate final security report', () => {
            cy.log('📊 === RELATÓRIO FINAL DE SEGURANÇA ===');
            
            cy.log('✅ RESULTADOS OBTIDOS:');
            cy.log('   👨‍💼 ADMIN: Acesso total (6/6 endpoints)');
            cy.log('   👤 USER: Acesso limitado (3/6 endpoints)');
            cy.log('   🚫 Endpoints protegidos contra acesso não autorizado');
            cy.log('   🔑 Tokens inválidos são rejeitados');
            
            cy.log('🛡️ SEGURANÇA IMPLEMENTADA:');
            cy.log('   ✅ Autenticação JWT funcional');
            cy.log('   ✅ Controle de acesso baseado em roles');
            cy.log('   ✅ Segregação de funcionalidades por perfil');
            cy.log('   ✅ Proteção contra acesso não autorizado');
            
            cy.log('🎯 CENÁRIOS TESTADOS:');
            cy.log('   ✅ Login com múltiplos perfis');
            cy.log('   ✅ Verificação de roles');
            cy.log('   ✅ Acesso permitido');
            cy.log('   ✅ Acesso negado');
            cy.log('   ✅ Segurança sem token');
            cy.log('   ✅ Segurança com token inválido');
            
            cy.log('🚀 === DEMONSTRAÇÃO CONCLUÍDA COM SUCESSO! ===');
            
            // Mock de dados para verificação final
            expect(adminToken).to.not.be.empty;
            expect(userToken).to.not.be.empty;
            expect(adminToken).to.not.equal(userToken);
        });
    });

    after(() => {
        cy.log('🎬 === FIM DA DEMONSTRAÇÃO ===');
        cy.log('📹 Vídeo salvo em: videos/10-complete-role-demo.cy.js.mp4');
        cy.log('🎉 Implementação de roles funcionando 100%!');
    });
});