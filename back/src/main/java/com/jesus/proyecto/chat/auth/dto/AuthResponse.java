package com.jesus.proyecto.chat.auth.dto;

import java.util.UUID;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class AuthResponse {
    private UUID id;
    private String usuario;
    private String nombre;
}
