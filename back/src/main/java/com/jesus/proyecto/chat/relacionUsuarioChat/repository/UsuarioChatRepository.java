package com.jesus.proyecto.chat.relacionUsuarioChat.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jesus.proyecto.chat.relacionUsuarioChat.entity.I_UsuarioChatId;
import com.jesus.proyecto.chat.relacionUsuarioChat.entity.UsuarioChat;

public interface UsuarioChatRepository extends JpaRepository<UsuarioChat, I_UsuarioChatId> {

    List<UsuarioChat> findById_IdUsuario(UUID idUsuario);

    List<UsuarioChat> findById_IdChat(UUID idChat);

    boolean existsById_IdUsuarioAndId_IdChat(UUID idUsuario, UUID idChat);

    Optional<UsuarioChat> findById_IdUsuarioAndId_IdChat(UUID idUsuario, UUID idChat);

}