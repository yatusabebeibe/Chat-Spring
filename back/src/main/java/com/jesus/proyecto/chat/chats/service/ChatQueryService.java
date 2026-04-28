package com.jesus.proyecto.chat.chats.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.jesus.proyecto.chat.chats.dto.ChatListResponse;
import com.jesus.proyecto.chat.chats.entity.Chat;
import com.jesus.proyecto.chat.chats.mapper.ChatMapper;
import com.jesus.proyecto.chat.chats.repository.ChatRepository;
import com.jesus.proyecto.chat.chats.utils.TipoChat;
import com.jesus.proyecto.chat.mensajes.entity.Mensaje;
import com.jesus.proyecto.chat.mensajes.mapper.MensajeMapper;
import com.jesus.proyecto.chat.mensajes.repository.MensajeRepository;
import com.jesus.proyecto.chat.relacionUsuarioChat.repository.UsuarioChatRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ChatQueryService {

    private final ChatRepository chatRepository;
    private final UsuarioChatRepository usuarioChatRepository;
    private final MensajeRepository mensajeRepository;
    private final MensajeMapper mensajeMapper;
    private final ChatMapper chatMapper;

    private ChatListResponse crearResponse(Chat chat) {
        ChatListResponse response = chatMapper.toListResponse(chat);

        Mensaje ultimo = mensajeRepository
                .findFirstByChatIdAndEliminadoFalseOrderByFechaEnvioDesc(chat.getId())
                .orElse(null);

        response.setUltimoMensaje(
            ultimo != null ?
                mensajeMapper.toResponseSinArchivos(ultimo) :
                null
        );

        return response;
    }
    private ChatListResponse crearResponseUsuario(Chat chat, UUID usuarioId) {
        ChatListResponse response = crearResponse(chat);
        response.setNombre(chat.getNombreConversacionParaUsuario(usuarioId));
        return response;
    }

    public List<ChatListResponse> obtenerListaChatsUsuario(UUID idUsuario) {
        return usuarioChatRepository.findParticipacionesConMiembros(idUsuario)
                .stream()
                .map(uc -> crearResponseUsuario(uc.getChat(), idUsuario))
                .toList();
    }

    private List<ChatListResponse> chatsARespuesta(List<Chat> chats) {
        return chats.stream().map(this::crearResponse).toList();
    }

    public List<ChatListResponse> obtenerTodos() {
        return chatsARespuesta( chatRepository.findAll() );
    }

    public List<ChatListResponse> obtenerTodosLosGrupos() {
        return chatsARespuesta( chatRepository.findByTipo(TipoChat.GRUPO) );
    }

    public List<ChatListResponse> obtenerTodasLasConversaciones() {
        return chatsARespuesta( chatRepository.findByTipo(TipoChat.CONVERSACION) );
    }

}
