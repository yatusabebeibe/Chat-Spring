package com.jesus.proyecto.chat.usuarios.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jesus.proyecto.chat._general.exceptions.MyAuthException;
import com.jesus.proyecto.chat._general.utils.Paginacion;
import com.jesus.proyecto.chat.relacionUsuarioChat.mapper.UsuarioChatMapper;
import com.jesus.proyecto.chat.relacionUsuarioChat.service.UsuarioChatService;
import com.jesus.proyecto.chat.usuarios.dto.UsuarioChatResponse;
import com.jesus.proyecto.chat.usuarios.dto.UsuarioRequest;
import com.jesus.proyecto.chat.usuarios.dto.UsuarioUpdateRequest;
import com.jesus.proyecto.chat.usuarios.entity.Usuario;
import com.jesus.proyecto.chat.usuarios.mapper.UsuarioMapper;
import com.jesus.proyecto.chat.usuarios.service.UsuarioService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;


@RestController
@RequestMapping("/api/usuario")
@AllArgsConstructor
public class UsuarioController {

    private final UsuarioMapper usuarioMapper;
    private final UsuarioService usuarioService;
    private final UsuarioChatMapper usuarioChatMapper;
    private final UsuarioChatService usuarioChatService;

    @GetMapping({"", "/"})
    public ResponseEntity<?> buscar(@ModelAttribute UsuarioRequest request, @RequestParam(required = false) UUID chatId) {

        if (chatId != null) {
            List<UsuarioChatResponse> response = usuarioChatService.obtenerMiembrosDeGrupo(chatId)
                    .stream()
                    .map(usuarioChatMapper::toResponse)
                    .toList();
            return ResponseEntity.ok(response);
        }

        int pagina = Paginacion.validarPagina(request.getPagina());
        int limite = Paginacion.validarLimite(request.getLimite());
        Direction orden = Paginacion.validarOrdenOAsc(request.getOrden());

        Pageable pageable = PageRequest.of(pagina, limite, Sort.by(orden, "usuario"));

        if (request.getId() != null) {
            Usuario usuario = usuarioService.buscarPorId(request.getId());
            return ResponseEntity.ok(usuarioMapper.toResponse(usuario));
        }
        
        if (request.getBuscar() != null && !request.getBuscar().isBlank()) {
            return ResponseEntity.ok(usuarioService.listarPorUsuarioYNombreResponse(request.getBuscar(), pageable));
        }

        return ResponseEntity.ok(usuarioService.obtenerTodos(pageable));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication auth) {

        Usuario usuario = usuarioService.buscarPorUsuario(auth.getName());

        if (usuario == null) {
            throw new MyAuthException("Usuario no autenticado");
        }

        return ResponseEntity.ok(usuarioMapper.toResponseEspecifico(usuario));
    }

    @PutMapping({"", "/"})
    public ResponseEntity<?> actualizar(
            @Valid @RequestBody UsuarioUpdateRequest request,
            Authentication auth
    ) {

        Usuario usuario = usuarioService.buscarPorUsuario(auth.getName());

        if (usuario == null) {
            throw new MyAuthException("Usuario no autenticado");
        }

        if (request.getNombre() == null || request.getNombre().isBlank()) {
            throw new MyAuthException("El nuevo nombre no puede estar vacío");
        }

        Usuario actualizado = usuarioService.actualizar(usuario.getId(), request.getNombre());

        return ResponseEntity.ok(usuarioMapper.toResponseEspecifico(actualizado));
    }
}
