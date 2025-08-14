-- =============================
-- Script para configurar controle de acesso baseado em roles
-- Cria perfis ADMIN e USER com permissões apropriadas
-- =============================

-- Inserir perfis ADMIN e USER
INSERT INTO perfil (nome, descricao, ativo, created_at, updated_at) VALUES
('ADMIN', 'Administrador do sistema com acesso total a todas as funcionalidades', true, NOW(), NOW()),
('USER', 'Usuário comum com acesso limitado às funcionalidades básicas', true, NOW(), NOW());

-- Inserir permissões específicas para os novos endpoints
INSERT INTO permissao (codigo, nome, descricao, modulo, ativo, created_at, updated_at) VALUES
-- Permissões administrativas
('ADMIN_DASHBOARD_ACCESS', 'Acesso ao Dashboard Administrativo', 'Permite acesso ao painel administrativo com métricas do sistema', 'ADMINISTRATIVO', true, NOW(), NOW()),
('ADMIN_USER_MANAGEMENT', 'Gerenciamento de Usuários', 'Permite visualizar e gerenciar todos os usuários do sistema', 'ADMINISTRATIVO', true, NOW(), NOW()),
('ADMIN_SYSTEM_CONFIG', 'Configuração do Sistema', 'Permite alterar configurações globais do sistema', 'ADMINISTRATIVO', true, NOW(), NOW()),

-- Permissões de usuário comum
('USER_DASHBOARD_ACCESS', 'Acesso ao Dashboard Pessoal', 'Permite acesso ao painel pessoal com informações do usuário', 'ADMINISTRATIVO', true, NOW(), NOW()),
('USER_PROFILE_ACCESS', 'Acesso ao Perfil Pessoal', 'Permite visualizar dados do próprio perfil', 'ADMINISTRATIVO', true, NOW(), NOW()),
('USER_PROFILE_UPDATE', 'Atualização do Perfil Pessoal', 'Permite atualizar dados do próprio perfil', 'ADMINISTRATIVO', true, NOW(), NOW());

-- Associar permissões ao perfil ADMIN (perfil_id = 1)
INSERT INTO perfil_permissao (perfil_id, permissao_id) 
SELECT 
    (SELECT id FROM perfil WHERE nome = 'ADMIN') as perfil_id,
    p.id as permissao_id
FROM permissao p 
WHERE p.codigo IN (
    'ADMIN_DASHBOARD_ACCESS', 
    'ADMIN_USER_MANAGEMENT', 
    'ADMIN_SYSTEM_CONFIG',
    'USER_DASHBOARD_ACCESS', 
    'USER_PROFILE_ACCESS', 
    'USER_PROFILE_UPDATE'
);

-- Associar permissões ao perfil USER (perfil_id = 2)
INSERT INTO perfil_permissao (perfil_id, permissao_id) 
SELECT 
    (SELECT id FROM perfil WHERE nome = 'USER') as perfil_id,
    p.id as permissao_id
FROM permissao p 
WHERE p.codigo IN (
    'USER_DASHBOARD_ACCESS', 
    'USER_PROFILE_ACCESS', 
    'USER_PROFILE_UPDATE'
);

-- Associar o usuário administrador existente ao perfil ADMIN
-- Assumindo que o usuário admin@freepharma.com tem id = 1
INSERT INTO usuario_perfil (usuario_id, perfil_id) 
SELECT 
    u.id as usuario_id,
    (SELECT id FROM perfil WHERE nome = 'ADMIN') as perfil_id
FROM usuario_administrador u 
WHERE u.email = 'admin@freepharma.com'
AND NOT EXISTS (
    SELECT 1 FROM usuario_perfil up 
    WHERE up.usuario_id = u.id 
    AND up.perfil_id = (SELECT id FROM perfil WHERE nome = 'ADMIN')
);

-- Criar um usuário comum para testes (se não existir)
-- Primeiro, verificar se existe uma farmácia para associar
DO $$
DECLARE
    farmacia_id_var BIGINT;
    unidade_id_var BIGINT;
    user_id_var BIGINT;
BEGIN
    -- Buscar ou criar farmácia de teste
    SELECT id INTO farmacia_id_var FROM farmacia WHERE cnpj = '12345678000123' LIMIT 1;
    
    IF farmacia_id_var IS NULL THEN
        INSERT INTO farmacia (razao_social, nome_fantasia, cnpj, inscricao_estadual, endereco, telefone_contato, email_contato, status, ativo, created_at, updated_at) 
        VALUES ('Farmácia Teste LTDA', 'Farmácia Teste', '12345678000123', '123456789', 'Rua Teste, 123', '11999999999', 'teste@farmacia.com', 'ATIVO', true, NOW(), NOW())
        RETURNING id INTO farmacia_id_var;
    END IF;
    
    -- Buscar ou criar unidade de teste
    SELECT id INTO unidade_id_var FROM unidade WHERE farmacia_id = farmacia_id_var LIMIT 1;
    
    IF unidade_id_var IS NULL THEN
        INSERT INTO unidade (tipo, razao_social, nome_fantasia, cnpj, endereco, telefone, email, status, farmacia_id, ativo, created_at, updated_at) 
        VALUES ('MATRIZ', 'Farmácia Teste LTDA', 'Farmácia Teste - Matriz', '12345678000123', 'Rua Teste, 123', '11999999999', 'matriz@farmacia.com', 'ATIVO', farmacia_id_var, true, NOW(), NOW())
        RETURNING id INTO unidade_id_var;
    END IF;
    
    -- Verificar se o usuário comum já existe
    SELECT id INTO user_id_var FROM usuario_administrador WHERE email = 'user@freepharma.com';
    
    IF user_id_var IS NULL THEN
        -- Criar usuário comum (senha: 123456 em BCrypt)
        INSERT INTO usuario_administrador (nome, cpf_cnpj, email, telefone, login, senha, data_cadastro, status, ultimo_acesso, autenticacao2fa, secreto2fa, ativo, created_at, updated_at) 
        VALUES ('Usuário Comum', '98765432101', 'user@freepharma.com', '11977777777', 'user@freepharma.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye7Ik50rOhkmq47BhqOKn91QZy7vbx5Aa', NOW(), 'ATIVO', NOW(), false, null, true, NOW(), NOW())
        RETURNING id INTO user_id_var;
        
        -- Associar usuário comum ao perfil USER
        INSERT INTO usuario_perfil (usuario_id, perfil_id) 
        VALUES (user_id_var, (SELECT id FROM perfil WHERE nome = 'USER'));
        
        -- Associar usuário comum à unidade
        INSERT INTO usuario_unidade_acesso (usuario_id, unidade_id) 
        VALUES (user_id_var, unidade_id_var);
    END IF;
    
END $$;

-- Verificação final - mostrar os dados criados
SELECT 
    'Perfis criados:' as tipo,
    p.id,
    p.nome,
    p.descricao
FROM perfil p 
WHERE p.nome IN ('ADMIN', 'USER')

UNION ALL

SELECT 
    'Usuários com perfis:' as tipo,
    u.id,
    u.nome,
    string_agg(p.nome, ', ') as perfis
FROM usuario_administrador u
JOIN usuario_perfil up ON u.id = up.usuario_id
JOIN perfil p ON up.perfil_id = p.id
WHERE u.email IN ('admin@freepharma.com', 'user@freepharma.com')
GROUP BY u.id, u.nome

UNION ALL

SELECT 
    'Permissões ADMIN:' as tipo,
    p.id,
    p.codigo,
    p.nome
FROM permissao p
JOIN perfil_permissao pp ON p.id = pp.permissao_id
JOIN perfil pf ON pp.perfil_id = pf.id
WHERE pf.nome = 'ADMIN'

UNION ALL

SELECT 
    'Permissões USER:' as tipo,
    p.id,
    p.codigo,
    p.nome
FROM permissao p
JOIN perfil_permissao pp ON p.id = pp.permissao_id
JOIN perfil pf ON pp.perfil_id = pf.id
WHERE pf.nome = 'USER';