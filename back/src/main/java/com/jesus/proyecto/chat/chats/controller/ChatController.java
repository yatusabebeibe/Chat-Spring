package com.jesus.proyecto.chat.chats.controller;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jesus.proyecto.chat._general.exceptions.MyAuthException;
import com.jesus.proyecto.chat.chats.dto.ChatListResponse;
import com.jesus.proyecto.chat.chats.dto.ChatRequest;
import com.jesus.proyecto.chat.chats.dto.ChatResponse;
import com.jesus.proyecto.chat.chats.dto.ChatUpdateRequest;
import com.jesus.proyecto.chat.chats.service.ChatQueryService;
import com.jesus.proyecto.chat.chats.service.ChatService;
import com.jesus.proyecto.chat.relacionUsuarioChat.service.UsuarioChatService;
import com.jesus.proyecto.chat.usuarios.entity.Usuario;
import com.jesus.proyecto.chat.usuarios.service.UsuarioService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/chat")
@AllArgsConstructor
public class ChatController {

    private final UsuarioChatService usuarioChatService;
    private final UsuarioService usuarioService;
    private final ChatService chatService;
    private final ChatQueryService chatQueryService;

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);


    @GetMapping({"", "/"})
    public List<ChatListResponse> obtenerTodosUsuario(
        Authentication auth,
        @RequestParam(required = false,name = "id") UUID chaId
    ) {
        Usuario usuario = usuarioService.buscarPorUsuario(auth.getName());

        if (usuarioChatService.usuarioEstaEnGrupo(chaId, usuario.getId())) {
            chatService.obtenerChatPorId(chaId);
        }
        return chatQueryService.obtenerListaChatsUsuario(usuario.getId());
    }

    @GetMapping("/all")
    public List<ChatListResponse> obtenerTodos() {
        return chatQueryService.obtenerTodos();
    }

    @PostMapping({"", "/"})
    public ChatResponse crear(@Valid @RequestBody ChatRequest chatRequest, Authentication auth) {
        String usuario = auth.getName();
        logger.info("El usuario {} intenta crear un {}",
            usuario,
            chatRequest.getTipo().toString().toLowerCase()
        );
        return chatService.crear(chatRequest, usuario);
    }


    @PutMapping({"", "/"})
    public ChatResponse actualizar(@Valid @RequestBody ChatUpdateRequest request, Authentication auth) {

        Usuario usuario = usuarioService.buscarPorUsuario(auth.getName());

        if (!usuarioChatService.esAdmin(usuario.getId(), request.getId())) {
            throw new MyAuthException("No puedes hacer esto");
        }

        return chatService.actualizar(request);
    }


}
