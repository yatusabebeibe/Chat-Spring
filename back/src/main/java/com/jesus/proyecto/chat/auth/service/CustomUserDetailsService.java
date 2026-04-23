package com.jesus.proyecto.chat.auth.service;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jesus.proyecto.chat.usuarios.entity.Usuario;
import com.jesus.proyecto.chat.usuarios.repository.UsuarioRepository;

import lombok.AllArgsConstructor;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return cargarUserDetails(usuario);
    }

    public UserDetails loadUserById(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario por ID no encontrado"));
        return cargarUserDetails(usuario);
    }

    private UserDetails cargarUserDetails(Usuario usuario) {
        // Roles simples: MIEMBRO o ADMIN (si estuviera en base de datos)
        List<GrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority("ROLE_MEMBER") // Default
        );

        return new User(
            usuario.getUsuario(),
            usuario.getPassword(), // BCrypt hash almacenado
            authorities
        );
    }
}
