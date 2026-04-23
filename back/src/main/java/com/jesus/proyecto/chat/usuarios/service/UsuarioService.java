package com.jesus.proyecto.chat.usuarios.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jesus.proyecto.chat._general.exceptions.AuthException;
import com.jesus.proyecto.chat._general.exceptions.DatosRequeridosException;
import com.jesus.proyecto.chat._general.exceptions.UsuarioNoEncontradoException;
import com.jesus.proyecto.chat._general.exceptions.UsuarioYaExisteException;
import com.jesus.proyecto.chat.usuarios.dto.RegistroRequest;
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

    public UsuarioResponse registro(RegistroRequest registroRequest) {

        if (usuarioRepository.existsByUsuario(registroRequest.getUsuario())) {
            throw new UsuarioYaExisteException();
        }

        Usuario usuario = usuarioMapper.toEntity(registroRequest);

        Usuario guardado = usuarioRepository.save(usuario);

        return usuarioMapper.toResponse(guardado);
    }

    public UsuarioResponse login(String nombreUsuario, String password) {

        Usuario usuario = usuarioRepository.findByUsuario(nombreUsuario)
                .orElseThrow(UsuarioNoEncontradoException::new);

        if (!usuario.getPassword().equals(password)) {
            throw new AuthException();
        }

        usuario.setFechaUltimaConexion(Instant.now());

        return usuarioMapper.toResponse(usuario);
    }

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

    public Usuario buscar(UUID id, String usuario) {

        if (id != null) {
            return usuarioRepository.findById(id).orElse(null);
        }

        if (usuario != null && !usuario.isBlank()) {
            return usuarioRepository.findByUsuario(usuario).orElse(null);
        }

        throw new DatosRequeridosException();
    }

}