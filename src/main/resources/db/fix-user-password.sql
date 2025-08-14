-- Corrigir senha do usuário USER para usar o mesmo hash do admin (senha: 123456)
UPDATE usuario_administrador 
SET senha = (SELECT senha FROM usuario_administrador WHERE email = 'admin@freepharma.com')
WHERE email = 'user@freepharma.com';

-- Verificar se funcionou
SELECT email, nome, senha FROM usuario_administrador WHERE email IN ('admin@freepharma.com', 'user@freepharma.com');