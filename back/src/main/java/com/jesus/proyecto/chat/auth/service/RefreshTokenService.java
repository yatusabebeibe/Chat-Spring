package com.jesus.proyecto.chat.auth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jesus.proyecto.chat.auth.entity.RefreshToken;
import com.jesus.proyecto.chat.auth.repository.RefreshTokenRepository;
import com.jesus.proyecto.chat.usuarios.entity.Usuario;
import com.jesus.proyecto.chat.usuarios.repository.UsuarioRepository;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UsuarioRepository usuarioRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, 
                                UsuarioRepository usuarioRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public void revocarRefreshdTodosUsuarios(String username) {
        Usuario u = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        RefreshToken token = refreshTokenRepository.findByToken(u.getId().toString())
            .orElse(null);
            
        if (token != null) {
            token.setValido(false);
            refreshTokenRepository.save(token);
        }
    }

    @Transactional
    public void limpiarRefreshTokensExpirados() {
        // Limpiar tokens expirados o inválidos periódicamente
    }
}
