package com.jesus.proyecto.chat.auth.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private UUID id;                    // ID del usuario (opcional)
    private String usuario;             // Username para display
    private String nombre;              // Nombre completo
    private String accessToken;         // Token para acceso al API
    private String refreshToken;        // Token para renovar access
}
