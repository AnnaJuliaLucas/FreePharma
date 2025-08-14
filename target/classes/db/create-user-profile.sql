-- Criar usuário com perfil USER para testes
INSERT INTO usuario_administrador (nome, cpf_cnpj, email, telefone, login, senha, data_cadastro, status, ultimo_acesso, autenticacao2fa, ativo, created_at, updated_at) 
VALUES ('Usuário Comum', '98765432101', 'user@freepharma.com', '11977777777', 'user@freepharma.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye7Ik50rOhkmq47BhqOKn91QZy7vbx5Aa', NOW(), 'ATIVO', NOW(), false, true, NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

-- Criar perfil USER se não existir
INSERT INTO perfil (nome, descricao, ativo, created_at, updated_at) 
VALUES ('USER', 'Usuário comum com acesso limitado', true, NOW(), NOW())
ON CONFLICT (nome) DO NOTHING;

-- Associar usuário comum ao perfil USER
INSERT INTO usuario_perfil (usuario_id, perfil_id) 
SELECT 
    u.id,
    (SELECT id FROM perfil WHERE nome = 'USER')
FROM usuario_administrador u 
WHERE u.email = 'user@freepharma.com'
AND NOT EXISTS (
    SELECT 1 FROM usuario_perfil up 
    WHERE up.usuario_id = u.id 
    AND up.perfil_id = (SELECT id FROM perfil WHERE nome = 'USER')
);

-- Verificar se funcionou
SELECT 
    u.email, 
    u.nome,
    p.nome as perfil
FROM usuario_administrador u
LEFT JOIN usuario_perfil up ON u.id = up.usuario_id
LEFT JOIN perfil p ON up.perfil_id = p.id
WHERE u.email IN ('admin@freepharma.com', 'user@freepharma.com')
ORDER BY u.email;