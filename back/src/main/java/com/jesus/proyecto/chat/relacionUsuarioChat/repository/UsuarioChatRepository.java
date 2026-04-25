package com.jesus.proyecto.chat.relacionUsuarioChat.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.jesus.proyecto.chat.chats.entity.Chat;
import com.jesus.proyecto.chat.relacionUsuarioChat.entity.I_UsuarioChatId;
import com.jesus.proyecto.chat.relacionUsuarioChat.entity.UsuarioChat;

public interface UsuarioChatRepository extends JpaRepository<UsuarioChat, I_UsuarioChatId> {

    List<UsuarioChat> findChatsByUsuarioId(UUID usuarioId);

    List<UsuarioChat> findById_IdChat(UUID idChat);

    boolean existsById_IdUsuarioAndId_IdChat(UUID idUsuario, UUID idChat);

    Optional<UsuarioChat> findChatsByUsuarioIdAndId_IdChat(UUID idUsuario, UUID idChat);

    @Query("SELECT COUNT(u) FROM UsuarioChat u WHERE u.id.idChat = :idChat")
    long contarUsuariosEnChat(UUID idChat);

    @Query("""
        SELECT uc.chat FROM UsuarioChat uc
        WHERE uc.chat.tipo = 'CONVERSACION'
            AND uc.usuario.id IN (:id1, :id2)
        GROUP BY uc.chat
        HAVING COUNT(DISTINCT uc.usuario.id) = 2
    """)
    Optional<Chat> findConversacionEntreUsuarios(UUID id1, UUID id2);

    @Query("""
        SELECT uc
        FROM UsuarioChat uc
        JOIN FETCH uc.chat c
        JOIN FETCH c.miembros m
        JOIN FETCH m.usuario
        WHERE uc.usuario.id = :idUsuario
    """)
    List<UsuarioChat> findParticipacionesConMiembros(UUID idUsuario);
}