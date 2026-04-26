package com.jesus.proyecto.chat.mensajes.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jesus.proyecto.chat._general.exceptions.MyAuthException;
import com.jesus.proyecto.chat.mensajes.service.MensajeService;
import com.jesus.proyecto.chat.relacionUsuarioChat.service.UsuarioChatService;
import com.jesus.proyecto.chat.usuarios.entity.Usuario;
import com.jesus.proyecto.chat.usuarios.service.UsuarioService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/msg")
@AllArgsConstructor
public class MensajeController {

    private final MensajeService mensajeService;
    private final UsuarioChatService usuarioChatService;
    private final UsuarioService usuarioService;


    @GetMapping({"", "/"})
    public ResponseEntity<?> obtener(
        Authentication auth,
        @RequestParam UUID chaId,
        @RequestParam(required = false) UUID msgId
    ) {
        Usuario usuario = usuarioService.buscarPorUsuario(auth.getName());

        if (usuarioChatService.usuarioEstaEnGrupo(chaId, usuario.getId())) {
            return ResponseEntity.ok(mensajeService.obtenerMensajesChat(chaId, msgId));
        }

        throw new MyAuthException("No estas en este chat");
    }
}
