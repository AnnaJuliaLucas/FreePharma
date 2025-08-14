-- Script para criar dados de teste com perfis ADMIN e USER
-- Senha padrão: 'password123' - Hash BCrypt

-- Inserir perfis
INSERT INTO perfil (id, nome, descricao, ativo, created_at, updated_at) VALUES
(1, 'ADMIN', 'Administrador do sistema com acesso total', true, NOW(), NOW()),
(2, 'USER', 'Usuário comum com acesso limitado', true, NOW(), NOW());

-- Inserir farmácia de teste
INSERT INTO farmacia (id, razao_social, nome_fantasia, cnpj, inscricao_estadual, endereco, telefone_contato, email_contato, status, ativo, created_at, updated_at) VALUES
(1, 'Farmácia Teste LTDA', 'Farmácia Teste', '12345678000123', '123456789', 'Rua Teste, 123', '11999999999', 'teste@farmacia.com', 'ATIVO', true, NOW(), NOW());

-- Inserir unidade de teste
INSERT INTO unidade (id, tipo, razao_social, nome_fantasia, cnpj, endereco, telefone, email, status, farmacia_id, ativo, created_at, updated_at) VALUES
(1, 'MATRIZ', 'Farmácia Teste LTDA', 'Farmácia Teste - Matriz', '12345678000123', 'Rua Teste, 123', '11999999999', 'matriz@farmacia.com', 'ATIVO', 1, true, NOW(), NOW());

-- Inserir usuário administrador
INSERT INTO usuario_administrador (id, nome, cpf_cnpj, email, telefone, login, senha, data_cadastro, status, ultimo_acesso, autenticacao2fa, secreto2fa, ativo, created_at, updated_at) VALUES
(1, 'Admin Teste', '12345678901', 'admin@teste.com', '11988888888', 'admin@teste.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye7Ik50rOhkmq47BhqOKn91QZy7vbx5Aa', NOW(), 'ATIVO', NOW(), false, null, true, NOW(), NOW()),
(2, 'User Teste', '98765432101', 'user@teste.com', '11977777777', 'user@teste.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye7Ik50rOhkmq47BhqOKn91QZy7vbx5Aa', NOW(), 'ATIVO', NOW(), false, null, true, NOW(), NOW());

-- Associar usuários aos perfis
INSERT INTO usuario_perfil (usuario_id, perfil_id) VALUES
(1, 1), -- Admin tem perfil ADMIN
(2, 2); -- User tem perfil USER

-- Associar usuários às unidades
INSERT INTO usuario_unidade_acesso (usuario_id, unidade_id) VALUES
(1, 1), -- Admin acessa unidade 1
(2, 1); -- User acessa unidade 1