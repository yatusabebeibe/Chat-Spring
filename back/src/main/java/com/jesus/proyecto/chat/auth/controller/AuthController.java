package com.jesus.proyecto.chat.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jesus.proyecto.chat.auth.dto.AuthRequest;
import com.jesus.proyecto.chat.auth.dto.AuthResponse;
import com.jesus.proyecto.chat.auth.dto.RegistroRequest;
import com.jesus.proyecto.chat.auth.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/registro")
    public AuthResponse registrar(@Valid @RequestBody RegistroRequest request, HttpServletResponse response) {
        return authService.registrar(request, response);
    }

    @PostMapping("/login")
    public AuthResponse autenticar(@Valid @RequestBody AuthRequest request, HttpServletResponse response) {
        return authService.autenticar(request, response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        authService.limpiarCookies(response);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        return authService.refrescarAccessToken(request, response);
    }
}
