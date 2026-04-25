package com.jesus.proyecto.chat.usuarios.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jesus.proyecto.chat.usuarios.dto.UsuarioRequest;
import com.jesus.proyecto.chat.usuarios.service.UsuarioService;

import lombok.AllArgsConstructor;


@RestController
@RequestMapping("/api/usuario")
@AllArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping({"", "/"})
    public ResponseEntity<?> buscar(@ModelAttribute() UsuarioRequest request) {

        if (request.getId() != null) {
            return ResponseEntity.ok( Map.of("a", "a") );
            // return ResponseEntity.ok(usuarioService.buscarPorId(request.getId()));
        }
        
        if (request.getBuscar() != null && !request.getBuscar().isBlank()) {
            // return ResponseEntity.ok( Map.of("b", "b") );
            return ResponseEntity.ok(usuarioService.listarPorUsuarioYNombreResponse(request.getBuscar()));
        }
        return ResponseEntity.ok( Map.of("c", "c") );

        // return ResponseEntity.ok(usuarioService.obtenerTodos());
    }

}
