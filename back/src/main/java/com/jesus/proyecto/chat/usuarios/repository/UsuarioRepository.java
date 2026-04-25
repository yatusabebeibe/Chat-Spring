package com.jesus.proyecto.chat.usuarios.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jesus.proyecto.chat.usuarios.entity.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    
    Optional<Usuario> findById(UUID id);

    Optional<Usuario> findByUsuario(String usuario);

    List<Usuario> findByNombreContainingIgnoreCase(String nombre);

    boolean existsByUsuario(String usuario);

    List<Usuario> searchByUsuarioContainingIgnoreCaseOrNombreContainingIgnoreCase(String usuario, String nombre);

    @Query("""
        SELECT u FROM Usuario u
        WHERE LOWER(u.usuario) LIKE LOWER(CONCAT('%', :texto, '%'))
        OR LOWER(u.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))
    """)
    // List<Usuario> buscar(String texto);
    // @Query("SELECT u FROM Usuario u WHERE UPPER(u.usuario) LIKE UPPER(:usuario) OR UPPER(u.nombre) LIKE UPPER(:nombre)")
    List<Usuario> findUsuariosPorNombreOUsuario(String texto);
    // List<Usuario> findUsuariosPorNombreOUsuario(String usuario, String nombre);
}
