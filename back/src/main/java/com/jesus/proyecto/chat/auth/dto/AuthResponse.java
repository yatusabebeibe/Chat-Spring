package com.jesus.proyecto.chat.auth.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private UUID id;
    private String usuario;
    private String nombre;
}
