package com.jesus.proyecto.chat.chats.service;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jesus.proyecto.chat._general.exceptions.ChatNoEncontradoException;
import com.jesus.proyecto.chat._general.exceptions.MismoUsuarioException;
import com.jesus.proyecto.chat._general.exceptions.UsuarioNoEncontradoException;
import com.jesus.proyecto.chat.chats.dto.ChatRequest;
import com.jesus.proyecto.chat.chats.dto.ChatResponse;
import com.jesus.proyecto.chat.chats.dto.ChatUpdateRequest;
import com.jesus.proyecto.chat.chats.entity.Chat;
import com.jesus.proyecto.chat.chats.mapper.ChatMapper;
import com.jesus.proyecto.chat.chats.repository.ChatRepository;
import com.jesus.proyecto.chat.chats.utils.TipoChat;
import com.jesus.proyecto.chat.relacionUsuarioChat.entity.UsuarioChat;
import com.jesus.proyecto.chat.relacionUsuarioChat.repository.UsuarioChatRepository;
import com.jesus.proyecto.chat.relacionUsuarioChat.service.UsuarioChatService;
import com.jesus.proyecto.chat.usuarios.entity.Usuario;
import com.jesus.proyecto.chat.usuarios.repository.UsuarioRepository;
import com.jesus.proyecto.chat.usuarios.utils.RolEnChat;

import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioChatRepository usuarioChatRepository;
    private final UsuarioChatService usuarioChatService;
    private final ChatMapper chatMapper;

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    public ChatResponse crear(ChatRequest chatRequest, String usuario) {
        Usuario creador = usuarioRepository.findByUsuario( usuario )
                .orElseThrow(() -> new UsuarioNoEncontradoException());

        Chat chat = switch (chatRequest.getTipo()) {
            case GRUPO -> crearGrupo(chatRequest.getNombre(), creador);
            case CONVERSACION -> obtenerOCrearConversacion( creador, chatRequest.getIdParticipante() );
        };

        logger.error("-----------------------------------------");
        logger.error("> {} <",chat.getFechaCreacion());
        logger.error("-----------------------------------------");

        return chatMapper.toResponse(chat);
    }

    private Chat crearChatBase(TipoChat tipo, Usuario creador) {
        return crearChatBase(tipo, creador, null);
    }

    private Chat crearChatBase(TipoChat tipo, Usuario creador, String nombre) {
        Chat chat = new Chat();
        chat.setTipo(tipo);
        chat.setCreador(creador);
        chat.setNombre(nombre);
        return chatRepository.save(chat);
    }

    private Chat crearGrupo(String nombreGrupo, Usuario creador) {
        Chat chat = crearChatBase(TipoChat.GRUPO, creador, nombreGrupo);
        logger.info("Se ha creado el grupo '{}' por el usuario con id '{}'", nombreGrupo, creador.getId());

        usuarioChatService.agregarUsuario(creador, chat, RolEnChat.ADMIN);

        return chat;
    }

    public Chat obtenerOCrearConversacion(Usuario usr1, UUID idUsr2) {
        if (usr1.getId().equals(idUsr2)) {
            throw new MismoUsuarioException();
        }
        Usuario usr2 = usuarioRepository.findById(idUsr2)
                .orElseThrow(() -> new UsuarioNoEncontradoException());

        // Busca si ya existe una conversacion entre ellos, si no existe la crea
        return chatRepository.findConversacionEntreUsuarios(usr1.getId(), usr2.getId())
            .orElseGet(() -> crearConversacion(usr1, usr2));
    }

    private Chat crearConversacion(Usuario usr1, Usuario usr2) {
        Chat chat = crearChatBase(TipoChat.CONVERSACION, usr1);

        logger.info("Se ha creado el la conversacion entre los usuarios con id '{}' y '{}'", usr1.getId(), usr2.getId());

        usuarioChatService.agregarUsuario(usr1, chat, RolEnChat.MIEMBRO);
        usuarioChatService.agregarUsuario(usr2, chat, RolEnChat.MIEMBRO);

        return chat;
    }

    public Chat obtenerChatPorId(UUID id) {
        return chatRepository
            .findById(id)
            .orElseThrow(() -> new ChatNoEncontradoException());
    }

    public List<Usuario> obtenerMiembrosDeGrupo(UUID chatId) {
        List<UsuarioChat> miembros = usuarioChatRepository.findById_IdChat(chatId);

        return miembros.stream().map(UsuarioChat::getUsuario).toList();
    }

    public ChatResponse actualizar(ChatUpdateRequest chatUpdateRequest) {
        Chat chat = chatRepository.findById(chatUpdateRequest.getId())
                .orElseThrow(() -> new ChatNoEncontradoException());

        chat.setNombre(chatUpdateRequest.getNombre());

        return chatMapper.toResponse(chat);
    }

    public void guardar(Chat chat) {
        chatRepository.save(chat);
    }
}
