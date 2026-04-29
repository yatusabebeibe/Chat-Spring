package com.jesus.proyecto.chat.mensajes.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jesus.proyecto.chat._general.ai.service.AIService;
import com.jesus.proyecto.chat._general.exceptions.MensajeNoEncontradoException;
import com.jesus.proyecto.chat._general.exceptions.MyAuthException;
import com.jesus.proyecto.chat.mensajes.entity.Mensaje;
import com.jesus.proyecto.chat.mensajes.service.MensajeService;
import com.jesus.proyecto.chat.relacionUsuarioChat.service.UsuarioChatService;
import com.jesus.proyecto.chat.usuarios.entity.Usuario;
import com.jesus.proyecto.chat.usuarios.service.UsuarioService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/msg/ai")
@AllArgsConstructor
public class MensajeAiController {

    private final MensajeService mensajeService;
    private final UsuarioChatService usuarioChatService;
    private final UsuarioService usuarioService;
    private final AIService aiService;


    @GetMapping("/responder")
    public ResponseEntity<?> resumir(@Valid @RequestParam UUID id, Authentication auth) {
        Mensaje mensaje = validar(id, auth);

        Map<String, String> respuesta = Map.of(
            "mensaje", aiService.responder(mensaje.getMensaje())
        );
        return ResponseEntity.ok(respuesta);
    }


    @GetMapping("/resumir")
    public ResponseEntity<?> contestar(@Valid @RequestParam UUID id, Authentication auth) {
        Mensaje mensaje = validar(id, auth);

        Map<String, String> respuesta = Map.of(
            "mensaje", aiService.resumirMensaje(mensaje.getMensaje())
        );
        return ResponseEntity.ok(respuesta);
    }


    // @GetMapping("/transcribir")
    // public ResponseEntity<?> transcribir(@Valid @RequestParam UUID id, Authentication auth) {
    //     Mensaje mensaje = validar(id, auth);

    //     Map<String, String> respuesta = Map.of("mensaje", aiService.(mensaje.getMensaje()));
    //     return ResponseEntity.ok(respuesta);
    // }




    private Mensaje validar(UUID msgId, Authentication auth) {
        Mensaje mensaje = mensajeService.obtenerMensaje(msgId);

        if (mensaje == null) {
            throw new MensajeNoEncontradoException();
        }

        Usuario usuario = usuarioService.buscarPorUsuario(auth.getName());

        if (!usuarioChatService.usuarioEstaEnGrupo(mensaje.getChat().getId(), usuario.getId())) {
            throw new MyAuthException("No estas en este chat");
        }

        return mensaje;
    }
}
