package com.jesus.proyecto.chat.usuarios.dto;

import com.jesus.proyecto.chat.usuarios.utils.UsuarioValidations;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistroRequest extends AuthRequest {

    @NotBlank(message = "Introduce tu nombre completo")
    @Size(
        min = UsuarioValidations.MIN_NOMBRE_LENGTH,
        max = UsuarioValidations.MAX_NOMBRE_LENGTH,
        message = UsuarioValidations.MSG_NOMBRE_LENGTH
    )
    private String nombre;

}