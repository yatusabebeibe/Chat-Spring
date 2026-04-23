package com.jesus.proyecto.chat.usuarios.entity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.jesus.proyecto.chat.relacionUsuarioChat.entity.UsuarioChat;
import com.jesus.proyecto.chat.usuarios.utils.UsuarioValidations;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = UsuarioValidations.MAX_USR_LENGTH, nullable = false, unique = true)
    @Pattern(regexp = UsuarioValidations.PATRON_USUARIO)
    private String usuario;

    @Column(length = UsuarioValidations.MAX_NOMBRE_LENGTH, nullable = false)
    @Pattern(regexp = UsuarioValidations.PATRON_NOMBRE)
    private String nombre;

    @Column(nullable = false, length = 60)
    private String password;

    @Column(nullable = false, columnDefinition = "DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP")
    @CreationTimestamp
    private Instant fechaCreacion = Instant.now();

    @Column(nullable = false, columnDefinition = "DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP")
    @CreationTimestamp
    private Instant fechaUltimaConexion;

    @Column(length = 20)
    private String avatar;

    @OneToMany(mappedBy = "usuario")
    private List<UsuarioChat> chats;
}
