package com.jesus.proyecto.chat.usuarios.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jesus.proyecto.chat.usuarios.entity.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    
    Optional<Usuario> findById(UUID id);

    Optional<Usuario> findByUsuario(String usuario);

    List<Usuario> findByNombreContainingIgnoreCase(String nombre);

    boolean existsByUsuario(String usuario);
}
