describe('FreePharma - Teste Básico de Autenticação', () => {
  const baseUrl = 'http://localhost:9876'

  it('Deve verificar se a API está respondendo', () => {
    cy.log('🔍 Verificando se a API está funcionando...')
    
    cy.request('GET', `${baseUrl}/api/setup/check`).then((response) => {
      expect(response.status).to.eq(200)
      cy.log('✅ API respondendo corretamente')
      cy.log(`📋 Response:`, response.body)
    })
  })

  it('Deve testar endpoint de login (sem credenciais)', () => {
    cy.log('🔐 Testando endpoint de login...')
    
    cy.request({
      method: 'POST',
      url: `${baseUrl}/api/auth/login`,
      body: {
        email: 'teste@inexistente.com',
        senha: 'senhaerrada'
      },
      failOnStatusCode: false
    }).then((response) => {
      cy.log(`📥 Status: ${response.status}`)
      cy.log(`📄 Body:`, response.body)
      
      // Deve retornar erro (400 ou 401)
      expect([400, 401]).to.include(response.status)
      cy.log('✅ Endpoint de login funcionando (rejeitou credenciais inválidas)')
    })
  })

  it('Deve testar acesso sem token (deve falhar)', () => {
    cy.log('🚫 Testando acesso sem token...')
    
    cy.request({
      method: 'GET',
      url: `${baseUrl}/api/admin/dashboard`,
      failOnStatusCode: false
    }).then((response) => {
      cy.log(`📥 Status: ${response.status}`)
      cy.log(`📄 Body:`, response.body)
      
      // Deve retornar 401 (Unauthorized)
      expect(response.status).to.eq(401)
      cy.log('✅ Segurança funcionando (acesso negado sem token)')
    })
  })

  it('Deve testar token inválido (deve falhar)', () => {
    cy.log('🚫 Testando token inválido...')
    
    cy.request({
      method: 'GET',
      url: `${baseUrl}/api/admin/dashboard`,
      headers: {
        'Authorization': 'Bearer token.invalido.fake'
      },
      failOnStatusCode: false
    }).then((response) => {
      cy.log(`📥 Status: ${response.status}`)
      cy.log(`📄 Body:`, response.body)
      
      // Deve retornar 401 (Unauthorized)
      expect(response.status).to.eq(401)
      cy.log('✅ Segurança funcionando (token inválido rejeitado)')
    })
  })

  it('Deve verificar endpoints públicos', () => {
    cy.log('🌐 Testando endpoints públicos...')
    
    const publicEndpoints = [
      '/api/setup/check',
      '/swagger-ui/',
      '/v2/api-docs'
    ]

    publicEndpoints.forEach(endpoint => {
      cy.request({
        method: 'GET',
        url: `${baseUrl}${endpoint}`,
        failOnStatusCode: false
      }).then((response) => {
        cy.log(`📋 ${endpoint}: Status ${response.status}`)
        
        // Endpoints públicos devem ser acessíveis (200, 302, etc.)
        expect(response.status).to.be.lessThan(400)
      })
    })

    cy.log('✅ Endpoints públicos acessíveis')
  })

  it('Deve mostrar resumo da aplicação', () => {
    cy.log('📊 ====== RESUMO DA APLICAÇÃO ======')
    cy.log('🌟 API FreePharma está funcionando!')
    cy.log('🔒 Segurança JWT ativa')
    cy.log('🚫 Acessos não autorizados bloqueados')
    cy.log('🌐 Endpoints públicos acessíveis')
    cy.log('📝 Para testes de permissão, configure dados de teste primeiro')
    cy.log('💡 Use: POST /api/setup/test-data')
  })
})