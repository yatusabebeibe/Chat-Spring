package com.jesus.proyecto.chat.usuarios.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jesus.proyecto.chat._general.exceptions.DatosRequeridosException;
import com.jesus.proyecto.chat._general.exceptions.UsuarioNoEncontradoException;
import com.jesus.proyecto.chat.usuarios.dto.UsuarioResponse;
import com.jesus.proyecto.chat.usuarios.entity.Usuario;
import com.jesus.proyecto.chat.usuarios.mapper.UsuarioMapper;
import com.jesus.proyecto.chat.usuarios.repository.UsuarioRepository;

import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    public Usuario obtenerUsuarioPorNombreUsuario(String nombreUsuario) {
        return usuarioRepository.findByUsuario(nombreUsuario)
                .orElseThrow(() -> new UsuarioNoEncontradoException());
    }

    public List<UsuarioResponse> obtenerTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(u -> usuarioMapper.toResponse(u))
                .toList();
    }

    public Usuario buscarUnico(UUID id, String usuario) {

        if (id != null) {
            return usuarioRepository.findById(id).orElse(null);
        }

        if (usuario != null && !usuario.isBlank()) {
            return usuarioRepository.findByUsuario(usuario).orElse(null);
        }

        throw new DatosRequeridosException();
    }

    public Usuario buscarPorId(UUID id) {
        return usuarioRepository.findById(id)
                .orElse(null);
    }

    public Usuario buscarPorUsuario(String usuario) {
        return usuarioRepository.findByUsuario(usuario)
                .orElse(null);
    }

    public List<Usuario> listarPorUsuarioYNombre(String texto) {
        return usuarioRepository
                // .searchByUsuarioContainingIgnoreCaseOrNombreContainingIgnoreCase(texto, texto)
                .findUsuariosPorNombreOUsuario(texto)
                .stream()
                .toList();
    }

    public List<UsuarioResponse> listarPorUsuarioYNombreResponse(String texto) {
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println(usuarioRepository
            // .searchByUsuarioContainingIgnoreCaseOrNombreContainingIgnoreCase(texto, texto)
            .findUsuariosPorNombreOUsuario(texto)
            .stream()
            .map(usuarioMapper::toResponse)
            .toList()
        );
        System.out.println();
        System.out.println();
        System.out.println();
        return usuarioRepository
                // .searchByUsuarioContainingIgnoreCaseOrNombreContainingIgnoreCase(texto, texto)
                .findUsuariosPorNombreOUsuario(texto)
                .stream()
                .map(usuarioMapper::toResponse)
                .toList();
    }

}