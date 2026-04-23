package com.jesus.proyecto.chat.chats.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.jesus.proyecto.chat.chats.dto.ChatListResponse;
import com.jesus.proyecto.chat.chats.entity.Chat;
import com.jesus.proyecto.chat.chats.repository.ChatRepository;
import com.jesus.proyecto.chat.chats.utils.TipoChat;
import com.jesus.proyecto.chat.mensajes.entity.Mensaje;
import com.jesus.proyecto.chat.mensajes.mapper.MensajeMapper;
import com.jesus.proyecto.chat.mensajes.repository.MensajeRepository;
import com.jesus.proyecto.chat.relacionUsuarioChat.entity.UsuarioChat;
import com.jesus.proyecto.chat.relacionUsuarioChat.repository.UsuarioChatRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ChatQueryService {

    private final ChatRepository chatRepository;
    private final UsuarioChatRepository usuarioChatRepository;
    private final MensajeRepository mensajeRepository;
    private final MensajeMapper mensajeMapper;

    private ChatListResponse crearResponse(Chat chat) {
        Mensaje ultimo = mensajeRepository
                .findFirstByChatIdAndEliminadoFalseOrderByFechaEnvioDesc(chat.getId())
                .orElse(null);

        ChatListResponse response = new ChatListResponse();
        response.setId(chat.getId());
        response.setNombre(chat.getNombre());
        response.setTipo(chat.getTipo());
        response.setUltimoMensaje(mensajeMapper.toResponse(ultimo));
        response.setIdCreador(chat.getCreador().getId());
        response.setFechaCreacion(chat.getFechaCreacion());

        return response;
    }

    public List<ChatListResponse> obtenerListaChatsUsuario(UUID idUsuario) {
        List<UsuarioChat> participaciones = usuarioChatRepository.findById_IdUsuario(idUsuario);

        return participaciones.stream()
                .map(uChat -> crearResponse(uChat.getChat()))
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
