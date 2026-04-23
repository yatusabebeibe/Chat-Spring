package com.jesus.proyecto.chat.usuarios.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jesus.proyecto.chat.usuarios.dto.RegistroRequest;
import com.jesus.proyecto.chat.usuarios.dto.UsuarioRequest;
import com.jesus.proyecto.chat.usuarios.dto.UsuarioResponse;
import com.jesus.proyecto.chat.usuarios.service.UsuarioService;

import lombok.AllArgsConstructor;


@RestController
@RequestMapping("/api/usuario")
@AllArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping({"", "/"})
    public ResponseEntity<?> obtener(@ModelAttribute UsuarioRequest request) {

        UUID id = request != null ? request.getId() : null;
        String usuario = request != null ? request.getUsuario() : null;

        if (id == null && (usuario == null || usuario.isBlank())) {
            return ResponseEntity.ok(usuarioService.obtenerTodos());
        }

        return ResponseEntity.ok(usuarioService.buscar(id, usuario));
    }

    @PostMapping({"", "/"})
    public UsuarioResponse registrar(@RequestBody RegistroRequest registroRequest) {
        return usuarioService.registro(registroRequest);
    }

}
