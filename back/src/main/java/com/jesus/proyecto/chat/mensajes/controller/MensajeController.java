package com.jesus.proyecto.chat.mensajes.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jesus.proyecto.chat._general.utils.UUIDv7;
import com.jesus.proyecto.chat.usuarios.dto.UsuarioRequest;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/msg")
@AllArgsConstructor
public class MensajeController {
    
    @GetMapping({"", "/"})
    public ResponseEntity<?> obtener(@ModelAttribute UsuarioRequest request) {

        return ResponseEntity.ok(UUIDv7.randomUUID());
    }
}
