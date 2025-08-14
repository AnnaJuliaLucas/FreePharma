# FreePharma API - Cypress Tests

Este projeto contém testes automatizados end-to-end para a API FreePharma usando Cypress.

## 📁 Estrutura do Projeto

```
cypress-tests/
├── e2e/                     # Testes E2E
│   ├── 01-authentication.cy.js        # Testes de autenticação
│   ├── 02-administrativo-farmacia.cy.js    # Testes de farmácia
│   ├── 03-administrativo-responsavel.cy.js # Testes de responsável
│   ├── 04-administrativo-usuario.cy.js     # Testes de usuário
│   ├── 05-estoque-fornecedor.cy.js        # Testes de fornecedor
│   ├── 06-fiscal-importacao-nfe.cy.js     # Testes de importação NFe
│   ├── 07-fiscal-nota-fiscal.cy.js        # Testes de nota fiscal
│   └── 09-role-based-access-control.cy.js # Testes de controle de acesso por roles
├── fixtures/                # Dados de teste
│   └── nfe-test.xml        # Arquivo XML de exemplo para testes NFe
├── support/                 # Comandos customizados
│   ├── commands.js         # Comandos customizados do Cypress
│   └── e2e.js             # Configurações globais
├── cypress.config.js       # Configuração do Cypress
├── package.json           # Dependências e scripts
└── README.md             # Este arquivo
```

## 🚀 Pré-requisitos

1. **Node.js** (versão 16+ recomendada)
2. **API FreePharma** rodando em `http://localhost:8080`
3. **Banco de dados** configurado e com dados de teste

## 📦 Instalação

1. Navegue até o diretório dos testes:
```bash
cd cypress-tests
```

2. Instale as dependências:
```bash
npm install
```

## 🎯 Executando os Testes

### Interface Gráfica (Cypress App)
```bash
npm run cypress:open
```

### Modo Headless (CI/CD)
```bash
# Todos os testes
npm run test:all

# Apenas testes de autenticação
npm run test:auth

# Apenas testes administrativos
npm run test:admin

# Apenas testes de estoque
npm run test:estoque

# Apenas testes fiscais
npm run test:fiscal

# Headless completo
npm run test:headless
```

### Execução Individual
```bash
# Executar um arquivo específico
npx cypress run --spec "cypress-tests/e2e/01-authentication.cy.js"
```

## 🔐 Autenticação nos Testes

Todos os testes utilizam autenticação JWT automática através do comando personalizado `cy.login()`:

```javascript
before(() => {
  cy.login(); // Login automático antes dos testes
});

it('should access protected endpoint', () => {
  cy.authenticatedRequest({
    method: 'GET',
    url: '/api/farmacias'
  }).then((response) => {
    expect(response.status).to.eq(200);
  });
});
```

### Credenciais Padrão
- **Email**: `admin@freepharma.com`
- **Senha**: `admin123`

Para usar credenciais diferentes:
```javascript
cy.login('outro@email.com', 'outrasenha');
```

## 📋 Cobertura de Testes

### 1. Autenticação (`01-authentication.cy.js`)
- ✅ Login com credenciais válidas
- ✅ Login com credenciais inválidas
- ✅ Refresh token
- ✅ Validação de token
- ✅ Logout

### 2. Domínio Administrativo

#### Farmácia (`02-administrativo-farmacia.cy.js`)
- ✅ CRUD completo de farmácias
- ✅ Busca por CNPJ e status
- ✅ Ativação/Inativação
- ✅ Validações de dados

#### Responsável (`03-administrativo-responsavel.cy.js`)
- ✅ CRUD completo de responsáveis
- ✅ Busca por CPF e email
- ✅ Validações de CPF, email e telefone
- ✅ Verificação de duplicidade

#### Usuário Administrador (`04-administrativo-usuario.cy.js`)
- ✅ CRUD completo de usuários
- ✅ Alteração de senha
- ✅ Atribuição de perfis
- ✅ Gestão de status (ativo/inativo/bloqueado)

### 3. Domínio Estoque

#### Fornecedor (`05-estoque-fornecedor.cy.js`)
- ✅ CRUD completo de fornecedores
- ✅ Busca por nome, CNPJ e status
- ✅ Ativação/Inativação/Bloqueio
- ✅ Validações de CNPJ e email

### 4. Domínio Fiscal

#### Importação NFe (`06-fiscal-importacao-nfe.cy.js`)
- ✅ Upload de arquivos XML válidos
- ✅ Validação de formato de arquivo
- ✅ Validação de tamanho de arquivo
- ✅ Tratamento de erros

#### Nota Fiscal (`07-fiscal-nota-fiscal.cy.js`)
- ✅ Listagem com paginação
- ✅ Busca por diversos critérios
- ✅ Consulta de itens e inconsistências
- ✅ Filtros por período, status e tipo

### 5. Controle de Acesso (`09-role-based-access-control.cy.js`)
- ✅ Acesso de administradores ao dashboard administrativo
- ✅ Negação de acesso de usuários comuns ao dashboard administrativo
- ✅ Acesso de usuários ao dashboard pessoal
- ✅ Gerenciamento de configurações do sistema (apenas admins)
- ✅ Atualização de perfil pessoal (ambos os tipos de usuário)
- ✅ Validação de tokens e tratamento de erros de autorização

## 🛠️ Comandos Personalizados

### `cy.login(email, senha)`
Realiza login e armazena tokens JWT automaticamente.

### `cy.authenticatedRequest(options)`
Faz requisições HTTP com token JWT incluído nos headers.

### `cy.createTestFarmacia()`
Cria uma farmácia de teste e retorna os dados.

### `cy.cleanupTestData(resourceType, id)`
Remove dados de teste criados durante os testes.

### `cy.uploadNFeFile(fileName)`
Faz upload de arquivo NFe XML.

## ⚙️ Configurações

### Cypress Config (`cypress.config.js`)
```javascript
{
  baseUrl: 'http://localhost:8080',
  video: true,
  screenshotOnRunFailure: true,
  requestTimeout: 10000,
  responseTimeout: 10000,
  retries: {
    runMode: 2,
    openMode: 0
  }
}
```

### Variáveis de Ambiente
- `baseUrl`: URL base da API (padrão: `http://localhost:8080`)

## 📊 Relatórios

### Execução Local
Os resultados dos testes são exibidos no terminal com:
- ✅ Testes que passaram
- ❌ Testes que falharam
- ⏱️ Tempo de execução
- 📹 Vídeos (pasta `videos/`)
- 📷 Screenshots de falhas (pasta `screenshots/`)

### CI/CD
Para integração contínua, use:
```bash
npm run test:headless
```

## 🐛 Solução de Problemas

### API não está rodando
```
Error: connect ECONNREFUSED 127.0.0.1:8080
```
**Solução**: Certifique-se de que a API FreePharma está rodando em `http://localhost:8080`

### Falha de autenticação
```
Expected status 200, but got 400
```
**Solução**: Verifique se as credenciais padrão estão corretas no banco de dados

### Timeout nos testes
```
Timed out retrying after 10000ms
```
**Solução**: Aumente os timeouts no `cypress.config.js` ou otimize a performance da API

### Falhas por dados pré-existentes
```
CNPJ já cadastrado para outra farmácia
```
**Solução**: Execute os testes em um ambiente limpo ou implemente melhor cleanup

## 🤝 Contribuindo

1. Adicione novos testes em arquivos separados na pasta `e2e/`
2. Use nomenclatura consistente: `NN-dominio-entidade.cy.js`
3. Implemente cleanup adequado com `after()` hooks
4. Documente comandos personalizados em `commands.js`
5. Mantenha este README atualizado

## 📝 Notas Importantes

- ⚠️ **Dados de Teste**: Os testes criam e removem dados. Use um banco de desenvolvimento.
- 🔄 **Cleanup**: Todos os testes implementam limpeza de dados no hook `after()`.
- 🚀 **Performance**: Testes executam em paralelo quando possível.
- 🛡️ **Segurança**: Tokens JWT são gerenciados automaticamente.
- 📱 **Cobertura**: Testes cobrem todos os endpoints principais da API.

## 📧 Suporte

Para dúvidas ou problemas com os testes, consulte:
1. Logs do Cypress no terminal
2. Screenshots e vídeos gerados
3. Documentação da API FreePharma
4. Este arquivo README