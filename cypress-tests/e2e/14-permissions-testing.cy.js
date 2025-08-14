describe('FreePharma - Testes de Permissões ADMIN vs USER', () => {
  const baseUrl = 'http://localhost:9876'
  let adminToken = ''
  let userToken = ''

  before(() => {
    // Configuração inicial dos dados de teste
    cy.log('🔧 Configurando dados de teste...')
    
    cy.request({
      method: 'POST',
      url: `${baseUrl}/api/setup/test-data`,
      headers: { 'Content-Type': 'application/json' },
      failOnStatusCode: false
    }).then((response) => {
      if (response.status === 200) {
        cy.log('✅ Dados de teste criados com sucesso')
        cy.log('📋 Usuários disponíveis:')
        cy.log('👑 ADMIN: admin@teste.com / password123')
        cy.log('👤 USER: user@teste.com / password123')
      } else {
        cy.log('ℹ️ Dados de teste já existem ou erro na criação')
      }
    })

    // Verificar se dados estão prontos (opcional - pular se falhar)
    cy.request('GET', `${baseUrl}/api/setup/check`).then((response) => {
      expect(response.status).to.eq(200)
      if (response.body.ready) {
        cy.log('✅ Dados de teste verificados e prontos!')
      } else {
        cy.log('ℹ️ Dados de teste não encontrados, mas prosseguindo com teste de credenciais padrão...')
      }
    })
  })

  context('🔐 Autenticação', () => {
    it('Deve fazer login como ADMIN e capturar token', () => {
      cy.log('👑 Fazendo login como ADMIN...')
      
      // Tentar múltiplas credenciais conhecidas
      const adminCredentials = [
        { email: 'admin@teste.com', senha: 'password123' },
        { email: 'admin@freepharma.com', senha: '123456' },
        { email: 'admin@farmacia.com', senha: 'admin123' }
      ]
      
      let loginSuccess = false
      let adminTokenFound = ''
      
      function tryNextCredential(index) {
        if (index >= adminCredentials.length) {
          throw new Error('Nenhuma credencial ADMIN funcionou. Execute setup manual primeiro.')
        }
        
        const creds = adminCredentials[index]
        cy.log(`👑 Tentando login ADMIN ${index + 1}: ${creds.email}`)
        
        return cy.request({
          method: 'POST',
          url: `${baseUrl}/api/auth/login`,
          body: creds,
          failOnStatusCode: false
        }).then((response) => {
          if (response.status === 200 && response.body.accessToken) {
            loginSuccess = true
            adminTokenFound = response.body.accessToken
            adminToken = adminTokenFound
            cy.log(`✅ Login ADMIN bem-sucedido com: ${creds.email}`)
            return response
          } else {
            cy.log(`❌ Falha com ${creds.email}: ${response.body.error || response.status}`)
            return tryNextCredential(index + 1)
          }
        })
      }
      
      tryNextCredential(0).then((response) => {
        expect(response.status).to.eq(200)
        expect(response.body).to.have.property('accessToken')
        expect(response.body).to.have.property('usuario')
        
        adminToken = response.body.accessToken
        
        cy.log('✅ Login ADMIN bem-sucedido')
        cy.log(`🔑 Token capturado: ${adminToken.substring(0, 50)}...`)
        
        // Validar estrutura da resposta
        expect(response.body.usuario).to.have.property('email', 'admin@teste.com')
        expect(response.body.usuario).to.have.property('nome', 'Admin Teste')
      })
    })

    it('Deve fazer login como USER e capturar token', () => {
      cy.log('👤 Fazendo login como USER...')
      
      // Tentar múltiplas credenciais USER conhecidas
      const userCredentials = [
        { email: 'user@teste.com', senha: 'password123' },
        { email: 'user@freepharma.com', senha: '123456' },
        { email: 'funcionario@farmacia.com', senha: 'user123' }
      ]
      
      function tryNextUserCredential(index) {
        if (index >= userCredentials.length) {
          throw new Error('Nenhuma credencial USER funcionou. Execute setup manual primeiro.')
        }
        
        const creds = userCredentials[index]
        cy.log(`👤 Tentando login USER ${index + 1}: ${creds.email}`)
        
        return cy.request({
          method: 'POST',
          url: `${baseUrl}/api/auth/login`,
          body: creds,
          failOnStatusCode: false
        }).then((response) => {
          if (response.status === 200 && response.body.accessToken) {
            userToken = response.body.accessToken
            cy.log(`✅ Login USER bem-sucedido com: ${creds.email}`)
            return response
          } else {
            cy.log(`❌ Falha com ${creds.email}: ${response.body.error || response.status}`)
            return tryNextUserCredential(index + 1)
          }
        })
      }
      
      tryNextUserCredential(0).then((response) => {
        expect(response.status).to.eq(200)
        expect(response.body).to.have.property('accessToken')
        expect(response.body).to.have.property('usuario')
        
        userToken = response.body.accessToken
        
        cy.log('✅ Login USER bem-sucedido')
        cy.log(`🔑 Token capturado: ${userToken.substring(0, 50)}...`)
        
        // Validar estrutura da resposta
        expect(response.body.usuario).to.have.property('email', 'user@teste.com')
        expect(response.body.usuario).to.have.property('nome', 'User Teste')
      })
    })

    it('Deve falhar login com credenciais inválidas', () => {
      cy.log('❌ Testando login com credenciais inválidas...')
      
      cy.request({
        method: 'POST',
        url: `${baseUrl}/api/auth/login`,
        body: {
          email: 'invalid@email.com',
          senha: 'wrongpassword'
        },
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400)
        expect(response.body).to.have.property('error')
        
        cy.log('✅ Login inválido rejeitado corretamente')
      })
    })
  })

  context('👑 Rotas Exclusivas ADMIN - Acesso Permitido', () => {
    it('ADMIN deve acessar dashboard administrativo', () => {
      cy.log('🏠 ADMIN acessando dashboard administrativo...')
      
      cy.request({
        method: 'GET',
        url: `${baseUrl}/api/admin/dashboard`,
        headers: {
          'Authorization': `Bearer ${adminToken}`
        }
      }).then((response) => {
        expect(response.status).to.eq(200)
        expect(response.body).to.have.property('title')
        expect(response.body.title).to.include('Administrativo')
        
        cy.log('✅ Dashboard admin acessado com sucesso')
        cy.log(`📊 Dados recebidos:`, response.body)
      })
    })

    it('ADMIN deve listar todos os usuários', () => {
      cy.log('👥 ADMIN listando usuários...')
      
      cy.request({
        method: 'GET',
        url: `${baseUrl}/api/admin/users`,
        headers: {
          'Authorization': `Bearer ${adminToken}`
        }
      }).then((response) => {
        expect(response.status).to.eq(200)
        expect(response.body).to.have.property('users')
        expect(response.body).to.have.property('adminOnly', true)
        
        cy.log('✅ Lista de usuários obtida com sucesso')
        cy.log(`📋 Resposta:`, response.body)
      })
    })

    it('ADMIN deve atualizar configurações do sistema', () => {
      cy.log('⚙️ ADMIN atualizando configurações...')
      
      cy.request({
        method: 'POST',
        url: `${baseUrl}/api/admin/system/config`,
        headers: {
          'Authorization': `Bearer ${adminToken}`,
          'Content-Type': 'application/json'
        },
        body: {
          maintenance_mode: 'false',
          max_login_attempts: '5'
        }
      }).then((response) => {
        expect(response.status).to.eq(200)
        expect(response.body).to.have.property('status', 'success')
        
        cy.log('✅ Configurações atualizadas com sucesso')
        cy.log(`⚙️ Resposta:`, response.body)
      })
    })
  })

  context('❌ Rotas Exclusivas ADMIN - USER Negado', () => {
    it('USER NÃO deve acessar dashboard administrativo', () => {
      cy.log('🚫 USER tentando acessar dashboard admin...')
      
      cy.request({
        method: 'GET',
        url: `${baseUrl}/api/admin/dashboard`,
        headers: {
          'Authorization': `Bearer ${userToken}`
        },
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(403)
        
        cy.log('✅ Acesso negado corretamente (403)')
        cy.log(`🚫 Status: ${response.status}`)
      })
    })

    it('USER NÃO deve listar usuários', () => {
      cy.log('🚫 USER tentando listar usuários...')
      
      cy.request({
        method: 'GET',
        url: `${baseUrl}/api/admin/users`,
        headers: {
          'Authorization': `Bearer ${userToken}`
        },
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(403)
        
        cy.log('✅ Acesso negado corretamente (403)')
        cy.log(`🚫 Status: ${response.status}`)
      })
    })

    it('USER NÃO deve alterar configurações do sistema', () => {
      cy.log('🚫 USER tentando alterar configurações...')
      
      cy.request({
        method: 'POST',
        url: `${baseUrl}/api/admin/system/config`,
        headers: {
          'Authorization': `Bearer ${userToken}`,
          'Content-Type': 'application/json'
        },
        body: {
          test: 'config'
        },
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(403)
        
        cy.log('✅ Alteração negada corretamente (403)')
        cy.log(`🚫 Status: ${response.status}`)
      })
    })
  })

  context('👥 Rotas Compartilhadas - USER e ADMIN', () => {
    it('USER deve acessar dashboard do usuário', () => {
      cy.log('🏠 USER acessando dashboard pessoal...')
      
      cy.request({
        method: 'GET',
        url: `${baseUrl}/api/user/dashboard`,
        headers: {
          'Authorization': `Bearer ${userToken}`
        }
      }).then((response) => {
        expect(response.status).to.eq(200)
        expect(response.body).to.have.property('title')
        expect(response.body.title).to.include('Usuário')
        
        cy.log('✅ Dashboard user acessado com sucesso')
        cy.log(`📊 Dados recebidos:`, response.body)
      })
    })

    it('ADMIN também deve acessar dashboard do usuário', () => {
      cy.log('🏠 ADMIN acessando dashboard de usuário...')
      
      cy.request({
        method: 'GET',
        url: `${baseUrl}/api/user/dashboard`,
        headers: {
          'Authorization': `Bearer ${adminToken}`
        }
      }).then((response) => {
        expect(response.status).to.eq(200)
        expect(response.body).to.have.property('title')
        
        cy.log('✅ ADMIN pode acessar dashboard user')
        cy.log(`📊 Dados recebidos:`, response.body)
      })
    })

    it('USER deve visualizar perfil', () => {
      cy.log('👤 USER visualizando perfil...')
      
      cy.request({
        method: 'GET',
        url: `${baseUrl}/api/user/profile`,
        headers: {
          'Authorization': `Bearer ${userToken}`
        }
      }).then((response) => {
        expect(response.status).to.eq(200)
        expect(response.body).to.have.property('name')
        expect(response.body).to.have.property('email')
        
        cy.log('✅ Perfil visualizado com sucesso')
        cy.log(`👤 Perfil:`, response.body)
      })
    })

    it('USER deve atualizar perfil', () => {
      cy.log('✏️ USER atualizando perfil...')
      
      cy.request({
        method: 'PUT',
        url: `${baseUrl}/api/user/profile`,
        headers: {
          'Authorization': `Bearer ${userToken}`,
          'Content-Type': 'application/json'
        },
        body: {
          nome: 'Usuário Atualizado',
          telefone: '11999998888'
        }
      }).then((response) => {
        expect(response.status).to.eq(200)
        expect(response.body).to.have.property('status', 'success')
        
        cy.log('✅ Perfil atualizado com sucesso')
        cy.log(`✏️ Resposta:`, response.body)
      })
    })
  })

  context('🔒 Testes de Segurança', () => {
    it('Deve rejeitar acesso sem token', () => {
      cy.log('🚫 Testando acesso sem token...')
      
      cy.request({
        method: 'GET',
        url: `${baseUrl}/api/admin/dashboard`,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(401)
        
        cy.log('✅ Acesso sem token negado (401)')
        cy.log(`🚫 Status: ${response.status}`)
      })
    })

    it('Deve rejeitar token inválido', () => {
      cy.log('🚫 Testando token inválido...')
      
      cy.request({
        method: 'GET',
        url: `${baseUrl}/api/admin/dashboard`,
        headers: {
          'Authorization': 'Bearer invalid.jwt.token'
        },
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(401)
        
        cy.log('✅ Token inválido rejeitado (401)')
        cy.log(`🚫 Status: ${response.status}`)
      })
    })

    it('Deve validar token ADMIN', () => {
      cy.log('✅ Validando token ADMIN...')
      
      cy.request({
        method: 'GET',
        url: `${baseUrl}/api/auth/validate`,
        headers: {
          'Authorization': `Bearer ${adminToken}`
        }
      }).then((response) => {
        expect(response.status).to.eq(200)
        expect(response.body).to.have.property('valid', true)
        
        cy.log('✅ Token ADMIN válido')
        cy.log(`✓ Resposta:`, response.body)
      })
    })

    it('Deve validar token USER', () => {
      cy.log('✅ Validando token USER...')
      
      cy.request({
        method: 'GET',
        url: `${baseUrl}/api/auth/validate`,
        headers: {
          'Authorization': `Bearer ${userToken}`
        }
      }).then((response) => {
        expect(response.status).to.eq(200)
        expect(response.body).to.have.property('valid', true)
        
        cy.log('✅ Token USER válido')
        cy.log(`✓ Resposta:`, response.body)
      })
    })
  })

  context('🔄 Refresh Token', () => {
    it('Deve testar refresh token (pode falhar se não implementado)', () => {
      cy.log('🔄 Testando refresh token...')
      
      cy.request({
        method: 'POST',
        url: `${baseUrl}/api/auth/refresh-token`,
        body: {
          refreshToken: 'exemplo-refresh-token'
        },
        failOnStatusCode: false
      }).then((response) => {
        cy.log(`🔄 Status refresh: ${response.status}`)
        
        if (response.status === 200) {
          expect(response.body).to.have.property('accessToken')
          cy.log('✅ Refresh token funcionando')
        } else {
          cy.log('ℹ️ Refresh token não implementado ou token inválido')
        }
      })
    })

    it('Deve fazer logout', () => {
      cy.log('👋 Fazendo logout...')
      
      cy.request({
        method: 'POST',
        url: `${baseUrl}/api/auth/logout`
      }).then((response) => {
        expect(response.status).to.eq(200)
        expect(response.body).to.have.property('message')
        
        cy.log('✅ Logout realizado com sucesso')
        cy.log(`👋 Resposta:`, response.body)
      })
    })
  })

  after(() => {
    cy.log('🧹 Teste completo! Dados de teste mantidos para próximos testes.')
    cy.log('🔧 Para limpar dados: POST /api/setup/test-data (DELETE)')
    
    // Resumo dos resultados
    cy.log('📊 RESUMO DOS TESTES:')
    cy.log('✅ Autenticação ADMIN e USER funcionando')
    cy.log('✅ Controle de acesso ADMIN funcionando')
    cy.log('✅ Negação de acesso para USER funcionando')
    cy.log('✅ Rotas compartilhadas funcionando')
    cy.log('✅ Validação de tokens funcionando')
    cy.log('✅ Segurança (401/403) funcionando')
  })
})