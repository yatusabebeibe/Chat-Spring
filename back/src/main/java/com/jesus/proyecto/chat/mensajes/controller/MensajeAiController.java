package com.jesus.proyecto.chat.mensajes.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jesus.proyecto.chat._general.ai.service.AIService;
import com.jesus.proyecto.chat._general.exceptions.MensajeNoEncontradoException;
import com.jesus.proyecto.chat._general.exceptions.MyAuthException;
import com.jesus.proyecto.chat.archivoMensaje.entity.ArchivoMensaje;
import com.jesus.proyecto.chat.archivoMensaje.repository.ArchivoMensajeRepository;
import com.jesus.proyecto.chat.archivoMensaje.utils.PathValidations;
import com.jesus.proyecto.chat.mensajes.entity.Mensaje;
import com.jesus.proyecto.chat.mensajes.service.MensajeService;
import com.jesus.proyecto.chat.mensajes.utils.TipoMensaje;
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
    private final ArchivoMensajeRepository archivoMensajeRepository;
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


    @GetMapping("/transcribir/{id}/{indice}")
    public ResponseEntity<?> transcribir(
            @PathVariable UUID id,
            @PathVariable int indice,
            Authentication auth
    ) {
        Mensaje mensaje = validar(id, auth);

        ArchivoMensaje archivoMsg = archivoMensajeRepository.findByMensajeIdAndIdIndice(id, indice);

        if (archivoMsg == null) {
            return ResponseEntity.notFound().build();
        }

        String url = archivoMsg.getUrl();

        Path filePath = Paths.get(
                PathValidations.BASE_UPLOAD,
                PathValidations.CARPETA_CHATS,
                mensaje.getChat().getId().toString(),
                archivoMsg.getUrl()
        ).normalize();

        if (!Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }

        String extension = url.substring(url.lastIndexOf(".") + 1).toLowerCase();

        TipoMensaje tipo = aiService.detectarTipoMensaje(List.of(extension));

        if (tipo != TipoMensaje.AUDIO) {
            return ResponseEntity.badRequest().body("El archivo no es de tipo audio");
        }

        String texto = aiService.transcribirAudio(filePath);

        return ResponseEntity.ok(Map.of("mensaje", texto));
    }




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
