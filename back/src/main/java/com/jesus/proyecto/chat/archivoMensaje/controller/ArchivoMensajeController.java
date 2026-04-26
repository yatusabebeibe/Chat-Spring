package com.jesus.proyecto.chat.archivoMensaje.controller;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jesus.proyecto.chat.relacionUsuarioChat.service.UsuarioChatService;
import com.jesus.proyecto.chat.usuarios.entity.Usuario;
import com.jesus.proyecto.chat.usuarios.service.UsuarioService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/archivos")
@AllArgsConstructor
public class ArchivoMensajeController {

    private final UsuarioChatService usuarioChatService;
    private final UsuarioService usuarioService;

    @GetMapping("/{chatId}/{archivo}")
    public ResponseEntity<Resource> getFile(
            @PathVariable UUID chatId,
            @PathVariable String archivo,
            Authentication auth) throws MalformedURLException {

        Usuario usuario = usuarioService.buscarPorUsuario(auth.getName());

        if (!usuarioChatService.usuarioEstaEnGrupo(chatId, usuario.getId())) {
            return ResponseEntity.status(403).build();
        }

        Path filePath = Paths.get("uploads")
                .resolve(chatId.toString())
                .resolve(archivo)
                .normalize();

        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        MediaType mediaType = MediaTypeFactory.getMediaType(resource)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + archivo + "\"")
                .body(resource);
    }
}
