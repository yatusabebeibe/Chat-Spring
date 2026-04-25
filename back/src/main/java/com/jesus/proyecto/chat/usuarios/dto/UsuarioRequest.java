package com.jesus.proyecto.chat.usuarios.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class UsuarioRequest {

    private UUID id;

    private String usuario;

    private String nombre;

    private String buscar;
}
