package com.jesus.proyecto.chat.archivoMensaje.controller;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.jesus.proyecto.chat._general.ai.service.AIService;
import com.jesus.proyecto.chat.archivoMensaje.utils.PathValidations;
import com.jesus.proyecto.chat.chats.entity.Chat;
import com.jesus.proyecto.chat.chats.service.ChatService;
import com.jesus.proyecto.chat.chats.utils.TipoChat;
import com.jesus.proyecto.chat.mensajes.utils.TipoMensaje;
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
    private final AIService aiService;
    private final ChatService chatService;

    @GetMapping("/{chatId}/{archivo}")
    public ResponseEntity<Resource> getFile(
            @PathVariable UUID chatId,
            @PathVariable String archivo,
            Authentication auth
        ) throws MalformedURLException
    {
        Usuario usuario = usuarioService.buscarPorUsuario(auth.getName());

        if (!usuarioChatService.usuarioEstaEnGrupo(chatId, usuario.getId())) {
            return ResponseEntity.status(403).build();
        }

        Path filePath = Paths.get(PathValidations.BASE_UPLOAD, PathValidations.CARPETA_CHATS)
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

    @GetMapping("/{chatId}")
    public ResponseEntity<Resource> getImagenChat(
            @PathVariable UUID chatId,
            Authentication auth
        ) throws MalformedURLException
    {
        Usuario usuario = usuarioService.buscarPorUsuario(auth.getName());

        if (!usuarioChatService.usuarioEstaEnGrupo(chatId, usuario.getId())) {
            return ResponseEntity.status(403).build();
        }
        
        Chat chat = chatService.obtenerChatPorId(chatId);
        boolean esGrupo = chat.getTipo() == TipoChat.GRUPO;

        String archivo = esGrupo ?
                "0."+chat.getExtensionImagen() :
                usuario.getId().toString();

        String segundoPath = esGrupo ? PathValidations.CARPETA_CHATS : PathValidations.CARPETA_USRS;

        Path basePath = Paths.get(PathValidations.BASE_UPLOAD, segundoPath);

        Path filePath = esGrupo
                ? basePath.resolve(chatId.toString()).resolve(archivo)
                : basePath.resolve(archivo);
        filePath = filePath.normalize();

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

    @PostMapping("/{chatId}")
    public ResponseEntity<?> uploadFile(
            @PathVariable UUID chatId,
            @RequestParam MultipartFile file,
            Authentication auth
        ) throws Exception
    {
        Usuario usuario = usuarioService.buscarPorUsuario(auth.getName());

        if (!usuarioChatService.usuarioEstaEnGrupo(chatId, usuario.getId())) {
            return ResponseEntity.status(403).build();
        }

        Chat chat = chatService.obtenerChatPorId(chatId);

        if (chat.getTipo() == TipoChat.CONVERSACION) {
            return ResponseEntity.badRequest().body("No se puede cambiar la imagen a una conversacion");
        }

        Path folder = Paths.get(PathValidations.BASE_UPLOAD, PathValidations.CARPETA_CHATS).resolve(chatId.toString());
        Files.createDirectories(folder);

        String originalName = file.getOriginalFilename();

        // -----------------------------
        // 1. sacar extensión
        // -----------------------------
        if (originalName == null || !originalName.contains(".")) {
            return ResponseEntity.badRequest().body("Tipo de archivo incorrecto");
        }

        String extension = originalName.substring(originalName.lastIndexOf(".") + 1);

        // -----------------------------
        // 2. IA decide si es imagen
        // -----------------------------
        TipoMensaje tipo = aiService.detectarTipoMensaje(List.of(extension));

        if (tipo != TipoMensaje.IMAGEN) {
            return ResponseEntity.badRequest().body("Solo se permiten imágenes");
        }

        try (Stream<Path> files = Files.list(folder)) {
            files.filter(p -> p.getFileName().toString().startsWith("0."))
                .forEach(p -> {
                    try {
                        Files.deleteIfExists(p);
                    } catch (Exception ignored) {}
                });
        }

        // -----------------------------
        // 3. guardar extensión en chat
        // -----------------------------
        chat.setExtensionImagen(extension);
        chatService.guardar(chat);

        // -----------------------------
        // 4. nombre final: 0.<ext>
        // -----------------------------
        String filename = "0." + extension;
        Path target = folder.resolve(filename);

        try (var inputStream = file.getInputStream()) {
            Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
        }

        return ResponseEntity.ok("Archivo subido correctamente");
    }

    @PostMapping("/usuario")
    public ResponseEntity<?> uploadImagenUsuario(
            @RequestParam MultipartFile file,
            Authentication auth
    ) throws Exception
    {
        Usuario usuarioAuth = usuarioService.buscarPorUsuario(auth.getName());

        Path folder = Paths.get(PathValidations.BASE_UPLOAD, PathValidations.CARPETA_USRS);
        Files.createDirectories(folder);

        String originalName = file.getOriginalFilename();

        if (originalName == null || !originalName.contains(".")) {
            return ResponseEntity.badRequest().body("Tipo de archivo incorrecto");
        }

        String extension = originalName.substring(originalName.lastIndexOf(".") + 1);

        TipoMensaje tipo = aiService.detectarTipoMensaje(List.of(extension));

        if (tipo != TipoMensaje.IMAGEN) {
            return ResponseEntity.badRequest().body("Solo se permiten imágenes");
        }

        // borrar anterior
        try (Stream<Path> files = Files.list(folder)) {
            files.filter(p -> p.getFileName().toString().startsWith(usuarioAuth.getId().toString() + "."))
                .forEach(p -> {
                    try {
                        Files.deleteIfExists(p);
                    } catch (Exception ignored) {}
                });
        }

        // guardar en BD
        usuarioAuth.setExtensionAvatar(extension);
        usuarioService.guardar(usuarioAuth);

        String filename = usuarioAuth.getId().toString() + "." + extension;
        Path target = folder.resolve(filename);

        try (var inputStream = file.getInputStream()) {
            Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
        }

        return ResponseEntity.ok("Imagen de usuario actualizada");
    }

    @GetMapping("/usuario/{usrId}")
    public ResponseEntity<Resource> getImagenUsuario(
            @PathVariable UUID usrId,
            Authentication auth
    ) throws MalformedURLException
    {
        if (auth == null || auth.getName() == null) {
            return ResponseEntity.status(401).build();
        }

        Usuario usuario = usuarioService.buscarPorId(usrId);

        if (usuario == null || usuario.getExtensionAvatar() == null) {
            return ResponseEntity.notFound().build();
        }

        String archivo = usrId.toString() + "." + usuario.getExtensionAvatar();

        Path filePath = Paths.get(PathValidations.BASE_UPLOAD, PathValidations.CARPETA_USRS)
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
