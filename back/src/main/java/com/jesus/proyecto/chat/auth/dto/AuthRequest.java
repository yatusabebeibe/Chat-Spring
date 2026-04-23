package com.jesus.proyecto.chat.auth.dto;

import com.jesus.proyecto.chat.usuarios.utils.UsuarioValidations;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequest {

    @NotBlank(message = "Se necesita el nombre de usuario")
    @Size(
        min = UsuarioValidations.MIN_USR_LENGTH,
        max = UsuarioValidations.MAX_USR_LENGTH,
        message = UsuarioValidations.MSG_USR_LENGTH
    )
    @Pattern(regexp = UsuarioValidations.PATRON_USUARIO)
    private String usuario;

    @NotBlank(message = "La contraseña no puede estar vacía")
    private String password;
}