package com.jesus.proyecto.chat.usuarios.dto;

import java.time.Instant;
import java.util.UUID;

import lombok.Data;

@Data
public class UsuarioEspecificoResponse {

    private UUID id;

    private String usuario;

    private String nombre;

    private String extensionAvatar;

    private Instant fechaCreacion;

    private Instant fechaUltimaConexion;
}
