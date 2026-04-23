package com.jesus.proyecto.chat.relacionUsuarioChat.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jesus.proyecto.chat._general.exceptions.ChatNoEncontradoException;
import com.jesus.proyecto.chat._general.exceptions.UsuarioNoEncontradoException;
import com.jesus.proyecto.chat.chats.entity.Chat;
import com.jesus.proyecto.chat.chats.repository.ChatRepository;
import com.jesus.proyecto.chat.chats.utils.TipoChat;
import com.jesus.proyecto.chat.relacionUsuarioChat.entity.UsuarioChat;
import com.jesus.proyecto.chat.relacionUsuarioChat.repository.UsuarioChatRepository;
import com.jesus.proyecto.chat.usuarios.entity.Usuario;
import com.jesus.proyecto.chat.usuarios.repository.UsuarioRepository;
import com.jesus.proyecto.chat.usuarios.utils.RolEnChat;

import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class UsuarioChatService {

    private final UsuarioChatRepository usuarioChatRepository;
    private final ChatRepository chatRepository;
    private final UsuarioRepository usuarioRepository;

    public void agregarUsuario(Usuario usr, Chat chat, RolEnChat rol) {
        UsuarioChat usrChat = UsuarioChat.crear(usr, chat, rol);
        if (!usuarioChatRepository.existsById_IdUsuarioAndId_IdChat(usr.getId(), chat.getId())) {
            usuarioChatRepository.save(usrChat);
        }
    }

    public void agregarUsuarios(Chat chat, Usuario... usuarios) {
        for (Usuario usr : usuarios) {
            agregarUsuario(usr, chat, RolEnChat.MIEMBRO);
        }
    }

    public void añadirMiembroGrupo(UUID chatId, UUID usuarioId) {
        Chat chat = chatRepository.findByIdAndTipo(chatId,TipoChat.GRUPO)
                .orElseThrow(() -> new ChatNoEncontradoException());
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException());

        agregarUsuario(usuario, chat, RolEnChat.MIEMBRO);
    }

    public boolean usuarioEstaEnGrupo(UUID chatId, UUID usuarioId) {        
        return usuarioChatRepository.existsById_IdUsuarioAndId_IdChat(usuarioId, chatId);
    }

    public boolean esAdmin(UUID idUsuario, UUID idChat) {

        UsuarioChat uc = usuarioChatRepository
            .findById_IdUsuarioAndId_IdChat(idUsuario, idChat)
            .orElse(null);

        return (uc != null) && (uc.getRol() == RolEnChat.ADMIN);
    }

}
