package com.jesus.proyecto.chat.mensajes.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jesus.proyecto.chat._general.exceptions.MyAuthException;
import com.jesus.proyecto.chat.mensajes.dto.MensajeRequest;
import com.jesus.proyecto.chat.mensajes.service.MensajeService;
import com.jesus.proyecto.chat.relacionUsuarioChat.service.UsuarioChatService;
import com.jesus.proyecto.chat.usuarios.entity.Usuario;
import com.jesus.proyecto.chat.usuarios.service.UsuarioService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/msg")
@AllArgsConstructor
public class MensajeController {

    private final MensajeService mensajeService;
    private final UsuarioChatService usuarioChatService;
    private final UsuarioService usuarioService;


    @GetMapping({"", "/"})
    public ResponseEntity<?> obtener(@Valid @ModelAttribute MensajeRequest request, Authentication auth) {
        Usuario usuario = usuarioService.buscarPorUsuario(auth.getName());

        if (!usuarioChatService.usuarioEstaEnGrupo(request.getChatId(), usuario.getId())) {
            throw new MyAuthException("No estas en este chat");
        }

        return ResponseEntity.ok(mensajeService.obtenerMensajesChat(request));
    }
}
