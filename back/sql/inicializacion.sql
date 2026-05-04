-- Script SQL de inicialización para base de datos MySQL del proyecto Chat
-- Este script crea todos los tablas y datos iniciales para probar el funcionamiento

-- ============================================
-- TABLA: USUARIOS
-- ============================================
CREATE TABLE IF NOT EXISTS usuarios (
    id                              BINARY(16) PRIMARY KEY,
    usuario                         VARCHAR(30) NOT NULL UNIQUE,
    nombre                          VARCHAR(75),
    password                        VARCHAR(60) NOT NULL,
    fecha_creacion                  TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    fecha_ultima_conexion           TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    avatar                          VARCHAR(20)
);

-- ============================================
-- TABLA: CHATS (conversaciones y grupos)
-- ============================================
CREATE TABLE IF NOT EXISTS chats (
    id                              BINARY(16) PRIMARY KEY,
    nombre                          VARCHAR(30),
    tipo                            VARCHAR(20) NOT NULL, -- CONVERSACION o GRUPO
    id_owner                      BINARY(16) NOT NULL,
    fecha_creacion                  TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    
    FOREIGN KEY (id_owner) REFERENCES usuarios(id)
);

-- ============================================
-- TABLA: USUARIO_CHAT (miembros de chats)
-- ============================================
CREATE TABLE IF NOT EXISTS usuario_chat (
    -- Clave compuesta para la tabla con clave embebida
    id_usuario                      BINARY(16),
    id_chat                         BINARY(16),
    
    fecha_union                     TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    rol                             VARCHAR(20) NOT NULL, -- MIEMBRO o ADMIN
    
    CONSTRAINT pk_usuario_chat PRIMARY KEY (id_usuario, id_chat),
    
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id),
    FOREIGN KEY (id_chat) REFERENCES chats(id)
);

-- ============================================
-- TABLA: MENSAJES
-- ============================================
CREATE TABLE IF NOT EXISTS mensajes (
    id                              BINARY(16) PRIMARY KEY,
    id_usuario                      BINARY(16) NOT NULL,
    id_chat                         BINARY(16) NOT NULL,
    
    tipo                            VARCHAR(20) NOT NULL, -- TEXTO, IMAGEN, VIDEO, ARCHIVO, AUDIO
    mensaje                         TEXT,
    url_archivo                     VARCHAR(255),
    fecha_envio                    TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    fecha_edicion                   TIMESTAMP(3),
    eliminado                       BOOLEAN NOT NULL DEFAULT FALSE,
    
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id),
    FOREIGN KEY (id_chat) REFERENCES chats(id)
);

-- ============================================
-- TABLA: MENSAJE_LEIDO (recibidos por usuario)
-- ============================================
CREATE TABLE IF NOT EXISTS mensaje_leido (
    -- Clave compuesta para la tabla con clave embebida
    id_usuario                      BINARY(16),
    id_mensaje                      BINARY(16),
    
    fecha_visto                     TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    
    CONSTRAINT pk_mensaje_leido PRIMARY KEY (id_usuario, id_mensaje),
    
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id),
    FOREIGN KEY (id_mensaje) REFERENCES mensajes(id)
);

-- ============================================
-- DATOS INICIALES: USUARIOS DE PRUEBA
-- ============================================
INSERT INTO usuarios (id, usuario, nombre, password, avatar) VALUES
(X'550e8400e29b41d4a716446655440001', 'usuario1', 'Juan Pérez', 
 '$2a$10$rX3.2K9vPzTnHjQwLmOpPeCqRst.UvwXyZ123456789abcdefghij', 'avatar1.jpg'),
(X'550e8400e29b41d4a716446655440002', 'usuario2', 'María García', 
 '$2a$10$rX3.2K9vPzTnHjQwLmOpPeCqRst.UvwXyZ123456789abcdefghij', 'avatar2.jpg'),
(X'550e8400e29b41d4a716446655440003', 'usuario3', 'Carlos López', 
 '$2a$10$rX3.2K9vPzTnHjQwLmOpPeCqRst.UvwXyZ123456789abcdefghij', 'avatar3.jpg'),
(X'550e8400e29b41d4a716446655440004', 'usuario4', 'Ana Martínez', 
 '$2a$10$rX3.2K9vPzTnHjQwLmOpPeCqRst.UvwXyZ123456789abcdefghij', 'avatar4.jpg'),
(X'550e8400e29b41d4a716446655440005', 'usuario5', 'Luis Rodríguez', 
 '$2a$10$rX3.2K9vPzTnHjQwLmOpPeCqRst.UvwXyZ123456789abcdefghij', 'avatar5.jpg');

-- ============================================
-- DATOS INICIALES: GRUPOS DE CHAT
-- ============================================
INSERT INTO chats (id, nombre, tipo, id_owner) VALUES
(X'660e8400e29b41d4a716446655440001', 'Grupo Desarrollo Java', 'GRUPO', X'550e8400e29b41d4a716446655440001'),
(X'660e8400e29b41d4a716446655440002', 'Grupo Proyecto Chat', 'GRUPO', X'550e8400e29b41d4a716446655440002'),
(X'660e8400e29b41d4a716446655440003', 'Grupo Backend Team', 'GRUPO', X'550e8400e29b41d4a716446655440003'),
(X'660e8400e29b41d4a716446655440004', 'Grupo Test Users', 'GRUPO', X'550e8400e29b41d4a716446655440004'),
(X'660e8400e29b41d4a716446655440005', 'Grupo Desarrollo Fullstack', 'GRUPO', X'550e8400e29b41d4a716446655440005');

-- ============================================
-- DATOS INICIALES: CONVERSACIONES 1 a 1
-- ============================================
INSERT INTO chats (id, nombre, tipo, id_owner) VALUES
(X'660e8400e29b41d4a716446655441001', 'Conversa Juan - María', 'CONVERSACION', X'550e8400e29b41d4a716446655440001'),
(X'660e8400e29b41d4a716446655441002', 'Conversa Juan - Carlos', 'CONVERSACION', X'550e8400e29b41d4a716446655440001'),
(X'660e8400e29b41d4a716446655441003', 'Conversa María - Carlos', 'CONVERSACION', X'550e8400e29b41d4a716446655440002'),
(X'660e8400e29b41d4a716446655441004', 'Conversa Ana - Luis', 'CONVERSACION', X'550e8400e29b41d4a716446655440004'),
(X'660e8400e29b41d4a716446655441005', 'Conversa Juan - Ana', 'CONVERSACION', X'550e8400e29b41d4a716446655440001'),
(X'660e8400e29b41d4a716446655441006', 'Conversa María - Ana', 'CONVERSACION', X'550e8400e29b41d4a716446655440002'),
(X'660e8400e29b41d4a716446655441007', 'Conversa Carlos - Luis', 'CONVERSACION', X'550e8400e29b41d4a716446655440003'),
(X'660e8400e29b41d4a716446655441008', 'Conversa Ana - María', 'CONVERSACION', X'550e8400e29b41d4a716446655440004');

-- ============================================
-- DATOS INICIALES: USUARIOS EN GRUPOS (Miembros)
-- ============================================
INSERT INTO usuario_chat (id_usuario, id_chat, fecha_union, rol) VALUES
-- Grupo Desarrollo Java
(X'550e8400e29b41d4a716446655440001', X'660e8400e29b41d4a716446655440001', CURRENT_TIMESTAMP(3), 'ADMIN'),
(X'550e8400e29b41d4a716446655440002', X'660e8400e29b41d4a716446655440001', CURRENT_TIMESTAMP(3), 'MIEMBRO'),
(X'550e8400e29b41d4a716446655440003', X'660e8400e29b41d4a716446655440001', CURRENT_TIMESTAMP(3), 'MIEMBRO'),
(X'550e8400e29b41d4a716446655440004', X'660e8400e29b41d4a716446655440001', CURRENT_TIMESTAMP(3), 'MIEMBRO'),
(X'550e8400e29b41d4a716446655440005', X'660e8400e29b41d4a716446655440001', CURRENT_TIMESTAMP(3), 'MIEMBRO'),
-- Grupo Proyecto Chat
(X'550e8400e29b41d4a716446655440001', X'660e8400e29b41d4a716446655440002', CURRENT_TIMESTAMP(3), 'MIEMBRO'),
(X'550e8400e29b41d4a716446655440002', X'660e8400e29b41d4a716446655440002', CURRENT_TIMESTAMP(3), 'ADMIN'),
(X'550e8400e29b41d4a716446655440003', X'660e8400e29b41d4a716446655440002', CURRENT_TIMESTAMP(3), 'MIEMBRO'),
(X'550e8400e29b41d4a716446655440004', X'660e8400e29b41d4a716446655440002', CURRENT_TIMESTAMP(3), 'MIEMBRO'),
(X'550e8400e29b41d4a716446655440005', X'660e8400e29b41d4a716446655440002', CURRENT_TIMESTAMP(3), 'MIEMBRO'),
-- Grupo Backend Team
(X'550e8400e29b41d4a716446655440001', X'660e8400e29b41d4a716446655440003', CURRENT_TIMESTAMP(3), 'MIEMBRO'),
(X'550e8400e29b41d4a716446655440002', X'660e8400e29b41d4a716446655440003', CURRENT_TIMESTAMP(3), 'MIEMBRO'),
(X'550e8400e29b41d4a716446655440003', X'660e8400e29b41d4a716446655440003', CURRENT_TIMESTAMP(3), 'ADMIN'),
(X'550e8400e29b41d4a716446655440004', X'660e8400e29b41d4a716446655440003', CURRENT_TIMESTAMP(3), 'MIEMBRO'),
(X'550e8400e29b41d4a716446655440005', X'660e8400e29b41d4a716446655440003', CURRENT_TIMESTAMP(3), 'MIEMBRO'),
-- Grupo Test Users
(X'550e8400e29b41d4a716446655440001', X'660e8400e29b41d4a716446655440004', CURRENT_TIMESTAMP(3), 'MIEMBRO'),
(X'550e8400e29b41d4a716446655440002', X'660e8400e29b41d4a716446655440004', CURRENT_TIMESTAMP(3), 'MIEMBRO'),
(X'550e8400e29b41d4a716446655440003', X'660e8400e29b41d4a716446655440004', CURRENT_TIMESTAMP(3), 'MIEMBRO'),
(X'550e8400e29b41d4a716446655440004', X'660e8400e29b41d4a716446655440004', CURRENT_TIMESTAMP(3), 'ADMIN'),
(X'550e8400e29b41d4a716446655440005', X'660e8400e29b41d4a716446655440004', CURRENT_TIMESTAMP(3), 'MIEMBRO'),
-- Grupo Desarrollo Fullstack
(X'550e8400e29b41d4a716446655440001', X'660e8400e29b41d4a716446655440005', CURRENT_TIMESTAMP(3), 'MIEMBRO'),
(X'550e8400e29b41d4a716446655440002', X'660e8400e29b41d4a716446655440005', CURRENT_TIMESTAMP(3), 'MIEMBRO'),
(X'550e8400e29b41d4a716446655440003', X'660e8400e29b41d4a716446655440005', CURRENT_TIMESTAMP(3), 'MIEMBRO'),
(X'550e8400e29b41d4a716446655440004', X'660e8400e29b41d4a716446655440005', CURRENT_TIMESTAMP(3), 'MIEMBRO'),
(X'550e8400e29b41d4a716446655440005', X'660e8400e29b41d4a716446655440005', CURRENT_TIMESTAMP(3), 'ADMIN');

-- ============================================
-- DATOS INICIALES: MENSAJES DE PRUEBA EN GRUPOS
-- ============================================
INSERT INTO mensajes (id, id_usuario, id_chat, tipo, mensaje, url_archivo, fecha_envio) VALUES
-- Grupo Desarrollo Java - Mensajes de bienvenida y prueba
(X'770e8400e29b41d4a716446655440001', X'550e8400e29b41d4a716446655440001', X'660e8400e29b41d4a716446655440001', 'TEXTO', 
 '¡Bienvenidos al grupo de Desarrollo Java!', NULL, CURRENT_TIMESTAMP(3)),
(X'770e8400e29b41d4a716446655440002', X'550e8400e29b41d4a716446655440002', X'660e8400e29b41d4a716446655440001', 'TEXTO', 
 'Hola a todos, ¿alguien sabe cómo implementar Spring Boot?', NULL, CURRENT_TIMESTAMP(3)),
(X'770e8400e29b41d4a716446655440003', X'550e8400e29b41d4a716446655440003', X'660e8400e29b41d4a716446655440001', 'TEXTO', 
 'Estoy revisando el código del nuevo módulo de autenticación.', NULL, CURRENT_TIMESTAMP(3)),
(X'770e8400e29b41d4a716446655440004', X'550e8400e29b41d4a716446655440004', X'660e8400e29b41d4a716446655440001', 'TEXTO', 
 '¡Qué bien! El test de integración está pasando. 🎉', NULL, CURRENT_TIMESTAMP(3)),
(X'770e8400e29b41d4a716446655440005', X'550e8400e29b41d4a716446655440005', X'660e8400e29b41d4a716446655440001', 'TEXTO', 
 'Necesito ayuda con la configuración de Docker Compose, ¿alguien puede echarme una mano?', NULL, CURRENT_TIMESTAMP(3)),

-- Grupo Proyecto Chat - Pruebas del chat en sí
(X'770e8400e29b41d4a716446655440006', X'550e8400e29b41d4a716446655440002', X'660e8400e29b41d4a716446655440002', 'TEXTO', 
 '¡Chat funcionando! 🚀 Puedo enviar mensajes.', NULL, CURRENT_TIMESTAMP(3)),
(X'770e8400e29b41d4a716446655440007', X'550e8400e29b41d4a716446655440001', X'660e8400e29b41d4a716446655440002', 'TEXTO', 
 'Correcto, el chat está operando correctamente.', NULL, CURRENT_TIMESTAMP(3)),
(X'770e8400e29b41d4a716446655440008', X'550e8400e29b41d4a716446655440003', X'660e8400e29b41d4a716446655440002', 'TEXTO', 
 'Estoy probando a ver si recibo notificaciones en tiempo real.', NULL, CURRENT_TIMESTAMP(3)),

-- Grupo Backend Team
(X'770e8400e29b41d4a716446655440009', X'550e8400e29b41d4a716446655440003', X'660e8400e29b41d4a716446655440003', 'TEXTO', 
 'Sesión de backend team iniciada. Vamos a discutir la nueva API REST.', NULL, CURRENT_TIMESTAMP(3)),
(X'770e8400e29b41d4a716446655440010', X'550e8400e29b41d4a716446655440001', X'660e8400e29b41d4a716446655440003', 'TEXTO', 
 'He subido la documentación de las nuevas endpoints a GitHub.', NULL, CURRENT_TIMESTAMP(3)),
(X'770e8400e29b41d4a716446655440011', X'550e8400e29b41d4a716446655440004', X'660e8400e29b41d4a716446655440003', 'TEXTO', 
 'Revisando el código, parece que la optimización de base de datos ya está funcionando.', NULL, CURRENT_TIMESTAMP(3));

-- ============================================
-- DATOS INICIALES: MENSAJES EN CONVERSACIONES 1 a 1
-- ============================================
INSERT INTO mensajes (id, id_usuario, id_chat, tipo, mensaje, url_archivo, fecha_envio) VALUES
-- Conversa Juan - María
(X'880e8400e29b41d4a716446655440001', X'550e8400e29b41d4a716446655440001', X'660e8400e29b41d4a716446655441001', 'TEXTO', 
 'Hola María, ¿cómo va el proyecto?', NULL, CURRENT_TIMESTAMP(3)),
(X'880e8400e29b41d4a716446655440002', X'550e8400e29b41d4a716446655440002', X'660e8400e29b41d4a716446655441001', 'TEXTO', 
 'Hola Juan, el proyecto va muy bien. Esté revisando las pruebas de integración.', NULL, CURRENT_TIMESTAMP(3)),
(X'880e8400e29b41d4a716446655440003', X'550e8400e29b41d4a716446655440001', X'660e8400e29b41d4a716446655441001', 'TEXTO', 
 'Genial! Cuéntame más sobre los resultados de las pruebas.', NULL, CURRENT_TIMESTAMP(3)),

-- Conversa Juan - Carlos
(X'880e8400e29b41d4a716446655440004', X'550e8400e29b41d4a716446655440001', X'660e8400e29b41d4a716446655441002', 'TEXTO', 
 'Carlos, ¿has visto el commit del nuevo feature?', NULL, CURRENT_TIMESTAMP(3)),
(X'880e8400e29b41d4a716446655440005', X'550e8400e29b41d4a716446655440003', X'660e8400e29b41d4a716446655441002', 'TEXTO', 
 'Sí, está bien. He hecho algunas pequeñas correcciones.', NULL, CURRENT_TIMESTAMP(3)),
(X'880e8400e29b41d4a716446655440006', X'550e8400e29b41d4a716446655440001', X'660e8400e29b41d4a716446655441002', 'TEXTO', 
 'Perfecto, gracias por revisar el código! 👍', NULL, CURRENT_TIMESTAMP(3)),

-- Conversa María - Carlos
(X'880e8400e29b41d4a716446655440007', X'550e8400e29b41d4a716446655440002', X'660e8400e29b41d4a716446655441003', 'TEXTO', 
 'Carlos, ¿podrías ayudarme con el diseño de la base de datos?', NULL, CURRENT_TIMESTAMP(3)),
(X'880e8400e29b41d4a716446655440008', X'550e8400e29b41d4a716446655440003', X'660e8400e29b41d4a716446655441003', 'TEXTO', 
 'Claro María, ¿necesitas ayuda con las relaciones?', NULL, CURRENT_TIMESTAMP(3)),
(X'880e8400e29b41d4a716446655440009', X'550e8400e29b41d4a716446655440002', X'660e8400e29b41d4a716446655441003', 'TEXTO', 
 'Sí, estoy un poco confundida con el foreign keys de la tabla mensajes.', NULL, CURRENT_TIMESTAMP(3)),

-- Conversa Ana - Luis
(X'880e8400e29b41d4a716446655440010', X'550e8400e29b41d4a716446655440004', X'660e8400e29b41d4a716446655441004', 'TEXTO', 
 'Luis, ¿has probado el deploy en producción?', NULL, CURRENT_TIMESTAMP(3)),
(X'880e8400e29b41d4a716446655440011', X'550e8400e29b41d4a716446655440005', X'660e8400e29b41d4a716446655441004', 'TEXTO', 
 'Todavía no, estoy revisando la configuración del servidor.', NULL, CURRENT_TIMESTAMP(3)),

-- Conversa Juan - Ana
(X'880e8400e29b41d4a716446655440012', X'550e8400e29b41d4a716446655440001', X'660e8400e29b41d4a716446655441005', 'TEXTO', 
 'Ana, ¿qué tal el nuevo diseño de la interfaz?', NULL, CURRENT_TIMESTAMP(3)),
(X'880e8400e29b41d4a716446655440013', X'550e8400e29b41d4a716446655440004', X'660e8400e29b41d4a716446655441005', 'TEXTO', 
 'Se ve muy bien! Me gusta la nueva paleta de colores.', NULL, CURRENT_TIMESTAMP(3));

-- ============================================
-- DATOS INICIALES: MENSAJES LEIDOS (RECIBIDO POR USUARIOS)
-- ============================================
INSERT INTO mensaje_leido (id_usuario, id_mensaje, fecha_visto) VALUES
-- Usuarios que leyeron algunos mensajes en el grupo Desarrollo Java
(X'550e8400e29b41d4a716446655440002', X'770e8400e29b41d4a716446655440001', CURRENT_TIMESTAMP(3)),
(X'550e8400e29b41d4a716446655440003', X'770e8400e29b41d4a716446655440001', CURRENT_TIMESTAMP(3)),
(X'550e8400e29b41d4a716446655440004', X'770e8400e29b41d4a716446655440002', CURRENT_TIMESTAMP(3)),
(X'550e8400e29b41d4a716446655440005', X'770e8400e29b41d4a716446655440003', CURRENT_TIMESTAMP(3)),

-- Usuarios que leyeron mensajes en el grupo Proyecto Chat
(X'550e8400e29b41d4a716446655440001', X'770e8400e29b41d4a716446655440006', CURRENT_TIMESTAMP(3)),
(X'550e8400e29b41d4a716446655440003', X'770e8400e29b41d4a716446655440007', CURRENT_TIMESTAMP(3)),

-- Usuarios que leyeron mensajes en conversaciones 1 a 1
(X'550e8400e29b41d4a716446655440002', X'880e8400e29b41d4a716446655440001', CURRENT_TIMESTAMP(3)),
(X'550e8400e29b41d4a716446655440002', X'880e8400e29b41d4a716446655440003', CURRENT_TIMESTAMP(3)),
(X'550e8400e29b41d4a716446655440001', X'880e8400e29b41d4a716446655440004', CURRENT_TIMESTAMP(3)),
(X'550e8400e29b41d4a716446655440003', X'880e8400e29b41d4a716446655440005', CURRENT_TIMESTAMP(3));

-- ============================================
-- COMANDOS ÚTILES PARA CONECTARSE A MYSQL
-- ============================================
-- Para ejecutar en Windows PowerShell:
-- mysql -u root -p database_name < archivo.sql

-- ============================================
-- CONSULTAS DE VERIFICACIÓN (OPCIONAL)
-- ============================================
/*
SELECT COUNT(*) AS total_usuarios FROM usuarios;
SELECT COUNT(*) AS total_chats FROM chats;
SELECT COUNT(*) AS total_mensajes FROM mensajes;
SELECT COUNT(*) AS total_miembros_en_chats FROM usuario_chat;
*/