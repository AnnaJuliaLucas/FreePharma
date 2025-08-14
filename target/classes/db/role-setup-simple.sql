-- =============================
-- Script simplificado para configurar roles ADMIN/USER
-- Compatível com qualquer PostgreSQL
-- =============================

-- 1. Inserir perfis ADMIN e USER
INSERT INTO perfil (nome, descricao, ativo, created_at, updated_at) 
VALUES 
('ADMIN', 'Administrador do sistema com acesso total', true, NOW(), NOW()),
('USER', 'Usuário comum com acesso limitado', true, NOW(), NOW())
ON CONFLICT (nome) DO NOTHING;

-- 2. Inserir permissões para os novos endpoints
INSERT INTO permissao (codigo, nome, descricao, modulo, ativo, created_at, updated_at) 
VALUES
-- Permissões administrativas
('ADMIN_DASHBOARD', 'Dashboard Administrativo', 'Acesso ao painel administrativo', 'ADMINISTRATIVO', true, NOW(), NOW()),
('ADMIN_USERS', 'Gerenciamento de Usuários', 'Gerenciar usuários do sistema', 'ADMINISTRATIVO', true, NOW(), NOW()),
('ADMIN_CONFIG', 'Configuração do Sistema', 'Alterar configurações do sistema', 'ADMINISTRATIVO', true, NOW(), NOW()),

-- Permissões de usuário comum  
('USER_DASHBOARD', 'Dashboard Pessoal', 'Acesso ao painel pessoal', 'ADMINISTRATIVO', true, NOW(), NOW()),
('USER_PROFILE', 'Perfil Pessoal', 'Gerenciar perfil pessoal', 'ADMINISTRATIVO', true, NOW(), NOW())
ON CONFLICT (codigo) DO NOTHING;

-- 3. Associar TODAS as permissões ao perfil ADMIN
INSERT INTO perfil_permissao (perfil_id, permissao_id) 
SELECT 
    (SELECT id FROM perfil WHERE nome = 'ADMIN'),
    id
FROM permissao 
WHERE codigo IN ('ADMIN_DASHBOARD', 'ADMIN_USERS', 'ADMIN_CONFIG', 'USER_DASHBOARD', 'USER_PROFILE')
ON CONFLICT DO NOTHING;

-- 4. Associar apenas permissões básicas ao perfil USER
INSERT INTO perfil_permissao (perfil_id, permissao_id) 
SELECT 
    (SELECT id FROM perfil WHERE nome = 'USER'),
    id
FROM permissao 
WHERE codigo IN ('USER_DASHBOARD', 'USER_PROFILE')
ON CONFLICT DO NOTHING;

-- 5. Associar usuário admin existente ao perfil ADMIN
INSERT INTO usuario_perfil (usuario_id, perfil_id) 
SELECT 
    u.id,
    (SELECT id FROM perfil WHERE nome = 'ADMIN')
FROM usuario_administrador u 
WHERE u.email = 'admin@freepharma.com'
ON CONFLICT DO NOTHING;

-- 6. Criar usuário comum se não existir
INSERT INTO usuario_administrador (nome, cpf_cnpj, email, telefone, login, senha, data_cadastro, status, ultimo_acesso, autenticacao2fa, ativo, created_at, updated_at) 
SELECT 
    'Usuário Comum',
    '98765432101', 
    'user@freepharma.com',
    '11977777777',
    'user@freepharma.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMye7Ik50rOhkmq47BhqOKn91QZy7vbx5Aa', -- senha: 123456
    NOW(),
    'ATIVO',
    NOW(),
    false,
    true,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM usuario_administrador WHERE email = 'user@freepharma.com');

-- 7. Associar usuário comum ao perfil USER
INSERT INTO usuario_perfil (usuario_id, perfil_id) 
SELECT 
    u.id,
    (SELECT id FROM perfil WHERE nome = 'USER')
FROM usuario_administrador u 
WHERE u.email = 'user@freepharma.com'
ON CONFLICT DO NOTHING;

-- 8. Verificação - Mostrar resultados
SELECT 'PERFIS CRIADOS' as categoria, nome, descricao FROM perfil WHERE nome IN ('ADMIN', 'USER')
UNION ALL
SELECT 'USUARIOS COM ROLES' as categoria, u.nome, string_agg(p.nome, ', ') 
FROM usuario_administrador u
JOIN usuario_perfil up ON u.id = up.usuario_id  
JOIN perfil p ON up.perfil_id = p.id
WHERE u.email IN ('admin@freepharma.com', 'user@freepharma.com')
GROUP BY u.nome;