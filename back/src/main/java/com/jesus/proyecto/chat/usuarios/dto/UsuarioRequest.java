package com.jesus.proyecto.chat.usuarios.dto;

import java.util.UUID;

import org.springframework.data.domain.Sort.Direction;

import lombok.Data;

@Data
public class UsuarioRequest {

    private UUID id;

    private String usuario;

    private String nombre;

    private String buscar;

    private Direction orden;
    private int pagina;
    private int limite;
}
