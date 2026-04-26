package com.jesus.proyecto.chat.usuarios.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jesus.proyecto.chat.usuarios.entity.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    Optional<Usuario> findByUsuario(String usuario);

    boolean existsByUsuario(String usuario);

    @Query("""
        SELECT u FROM Usuario u
        WHERE LOWER(u.usuario) LIKE LOWER(CONCAT('%', :texto, '%'))
        OR LOWER(u.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))
    """)
    List<Usuario> findUsuariosPorNombreOUsuario(String texto, Pageable pageable);
}
