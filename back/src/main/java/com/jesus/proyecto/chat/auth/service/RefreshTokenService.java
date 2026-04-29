package com.jesus.proyecto.chat.auth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jesus.proyecto.chat.auth.entity.RefreshToken;
import com.jesus.proyecto.chat.auth.repository.RefreshTokenRepository;
import com.jesus.proyecto.chat.usuarios.entity.Usuario;
import com.jesus.proyecto.chat.usuarios.repository.UsuarioRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public void revocarRefreshdTodosUsuarios(String username) {
        Usuario u = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        RefreshToken token = refreshTokenRepository.findByToken(u.getId().toString())
            .orElse(null);
            
        if (token != null) {
            token.setRevocado(true);
            refreshTokenRepository.save(token);
        }
    }
}
