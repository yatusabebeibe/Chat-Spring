-- ============================================
-- DROP TABLAS 
-- ============================================
DROP TABLE IF EXISTS refresh_tokens;
DROP TABLE IF EXISTS archivo_mensaje;
DROP TABLE IF EXISTS mensaje_leido;
DROP TABLE IF EXISTS usuario_chat;
DROP TABLE IF EXISTS mensajes;
DROP TABLE IF EXISTS chats;
DROP TABLE IF EXISTS usuarios;


SELECT CURRENT_TIMESTAMP(6);

SHOW CREATE TABLE chats;

INSERT INTO chats (id, fecha_creacion, nombre, tipo, id_owner)
VALUES (UUID(), CURRENT_TIMESTAMP(6), 'test', 'GRUPO', UUID());