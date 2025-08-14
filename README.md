<p align="center">
  <a href="https://www.ufjf.br" rel="noopener" target="_blank">
    <img width="261" height="148" src="https://upload.wikimedia.org/wikipedia/commons/thumb/7/71/Logo_da_UFJF.png/640px-Logo_da_UFJF.png" alt="Logo UFJF" />
  </a>
</p>

<h1 align="center">FreePharma</h1>
<p align="center">Sistema de Gerenciamento de Notas Fiscais para Farmácias</p>

<div align="center">
  
  <!-- Vídeo demonstração -->
  <a href="____" target="_blank"><img alt="Vídeo YouTube" src="https://img.shields.io/badge/YouTube-Demonstração-FF0000?logo=youtube&logoColor=white"></a>
  
</div>

---

O **FreePharma** é um sistema de gestão de notas fiscais voltado para **farmácias**, desenvolvido como projeto de estudo da disciplina **DCC280 - Desenvolvimento Web Back-end** da **UFJF**.

Este projeto tem como foco:
- A aplicação prática de conceitos de desenvolvimento back-end
- A implementação de boas práticas de programação e segurança  

A plataforma permite importar, organizar e analisar **NF-e e NFC-e**, facilitando o cumprimento de obrigações como **SPED Fiscal, Sintegra e Declaração de IR**.

Além de centralizar o gerenciamento das notas, o FreePharma identifica inconsistências tributárias e gera relatórios inteligentes que apoiam a tomada de decisões contábeis.

## 🏗️ Arquitetura

O FreePharma implementa **Clean Architecture** utilizando Spring Boot, seguindo princípios de Domain-Driven Design (DDD) com clara separação de responsabilidades:

- **Domain Layer**: Entidades, value objects e interfaces de repositório
- **Application Layer**: serviços de negócio  
- **API Layer**: Controllers REST e configurações
- **Infrastructure**: Implementações de repositório via Spring Data JPA

### Tecnologias Principais

- **Backend**: Spring Boot 2.5.0 com Java 17
- **Banco de Dados**: PostgreSQL
- **Segurança**: Spring Security com JWT
- **Documentação**: Swagger 2
- **Testes**: Cypress para testes E2E

## 🔐 Camada de Segurança

O **FreePharma** foi projetado para garantir que todos os dados fiscais sejam tratados com máxima segurança.

No vídeo de demonstração (link acima), é possível visualizar:
- O processo de **login seguro** com autenticação JWT
- O controle de acesso baseado em perfis de usuário
- A proteção contra inconsistências e manipulações indevidas
- Como a camada de segurança integra-se às funcionalidades fiscais

### Recursos de Segurança

- Autenticação JWT stateless
- Controle de acesso baseado em roles (RBAC)
- Proteção de endpoints sensíveis
- Criptografia de dados fiscais

## 🎯 Objetivos Principais

### Primeira Fase (Implementada)
- Cadastro das entidades principais para o correto funcionamento da importação e gerência de notas fiscais
- Implementação da arquitetura base do sistema
- Configuração da segurança e autenticação
- Desenvolvimento dos CRUDs fundamentais

### Segunda Fase (Em Desenvolvimento)
- Importação e armazenamento centralizado de **NF-e** e **NFC-e**
- Detecção automática de inconsistências fiscais:
  - Divergências de valores
  - Erros de classificação tributária (**NCM, CFOP, CST**)
  - Notas duplicadas
- Geração de **relatórios inteligentes** com alertas de irregularidades
- Apoio no cumprimento de **obrigações fiscais e contábeis**
- Maior **segurança e eficiência** no processo de escrituração

## 🚀 Como Executar

### Pré-requisitos

- Java 17 ou superior
- Maven 3.6+
- PostgreSQL 12+

### Configuração do Banco de Dados

1. Crie um banco PostgreSQL chamado `FreePharma` (ou configure conforme necessário)
2. Configure as credenciais em `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/FreePharma
   spring.datasource.username=postgres
   spring.datasource.password=postgres
   ```

### Executando a Aplicação

```bash
# Compilar o projeto
mvn clean compile

# Executar testes
mvn test

# Executar a aplicação
mvn spring-boot:run

# Empacotar a aplicação
mvn clean package
```

A aplicação estará disponível em `http://localhost:9876`

## 📚 Documentação da API

- **Swagger UI**: `http://localhost:9876/swagger-ui/`
- **API Docs**: `http://localhost:9876/v3/api-docs/`

## 🧪 Testes

O projeto inclui testes unitários e de integração:

```bash
# Executar todos os testes
mvn test

# Testes E2E com Cypress
cd cypress-tests
npm install
npm run cy:run
```

### Testes de Permissões

O sistema inclui testes específicos para validação do controle de acesso:

```bash
# Executar testes de permissões
./run-permissions-tests.bat
```

## 🏢 Domínios de Negócio

### 1. Administrativo
- Gerenciamento de usuários e farmácias
- Controle de unidades e responsáveis
- Sistema de autenticação e autorização

### 2. Estoque
- Gerenciamento de produtos e fornecedores
- Controle de ajustes de estoque
- Histórico de valores de produtos

### 3. Fiscal
- Processamento de notas fiscais (NF-e/NFC-e)
- Detecção de inconsistências tributárias
- Relatórios e dashboards fiscais

## 📋 Funcionalidades Principais

### Gestão de Farmácias
- Cadastro completo de farmácias multi-tenant
- Gerenciamento de unidades e responsáveis
- Controle de usuários administradores

### Importação de NF-e
- Upload e processamento de arquivos XML
- Validação automática de dados fiscais
- Detecção de inconsistências em tempo real

### Relatórios Inteligentes
- Dashboard com métricas fiscais
- Alertas de irregularidades
- Suporte para obrigações acessórias (SPED, Sintegra)

## 👥 Contribuidores

- Anna Julia de Almeida Lucas 
- Hugo Amoglia Priori



---

<p align="center">
  Desenvolvido com ❤️ para automatizar a gestão fiscal de farmácias
</p>