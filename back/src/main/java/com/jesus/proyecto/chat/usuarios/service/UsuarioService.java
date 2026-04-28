package com.jesus.proyecto.chat.usuarios.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
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

    public List<UsuarioResponse> obtenerTodos(Pageable pageable) {
        return usuarioRepository.findAll(pageable)
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

    public List<Usuario> listarPorUsuarioYNombre(String texto, Pageable pageable) {
        return usuarioRepository
                .findUsuariosPorNombreOUsuario(texto, pageable)
                .stream()
                .toList();
    }

    public List<UsuarioResponse> listarPorUsuarioYNombreResponse(String texto, Pageable pageable) {
        return usuarioRepository
                .findUsuariosPorNombreOUsuario(texto, pageable)
                .stream()
                .map(usuarioMapper::toResponse)
                .toList();
    }

    public void guardar(Usuario usuarioAuth) {
        usuarioRepository.save(usuarioAuth);
    }

    public Usuario actualizar(UUID id, String nombre) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException());

        usuario.setNombre(nombre);

        return usuarioRepository.save(usuario);
    }
}