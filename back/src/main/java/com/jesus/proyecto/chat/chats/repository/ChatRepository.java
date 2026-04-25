package com.jesus.proyecto.chat.chats.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jesus.proyecto.chat.chats.entity.Chat;
import com.jesus.proyecto.chat.chats.utils.TipoChat;

@Repository
public interface ChatRepository extends JpaRepository<Chat, UUID> {

    Optional<Chat> findById(UUID id);

    List<Chat> findByTipo(TipoChat tipo);

    Optional<Chat> findByIdAndTipo(UUID id, TipoChat tipo);

    // busqueda con @query para buscar si una conversacion existe entre dos usuarios
    // uniendo la tabla de chats con la tabla de relacionUsuarioChat y filtrando por
    // el id de los usuarios
    @Query("""
        SELECT c FROM Chat c
            JOIN UsuarioChat uc ON uc.chat = c
            JOIN Usuario u ON uc.usuario = u
        WHERE c.tipo = 'CONVERSACION'
            AND u.id IN (:id1, :id2)
        GROUP BY c
        HAVING COUNT(DISTINCT u.id) = 2
    """)
    Optional<Chat> findConversacionEntreUsuarios(UUID id1, UUID id2);

}
