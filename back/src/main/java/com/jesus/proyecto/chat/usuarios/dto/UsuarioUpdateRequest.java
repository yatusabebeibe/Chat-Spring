package com.jesus.proyecto.chat.usuarios.dto;

import com.jesus.proyecto.chat.usuarios.utils.UsuarioValidations;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsuarioUpdateRequest {

    @NotBlank(message = "Introduce tu nombre completo")
    @Size(
        min = UsuarioValidations.MIN_NOMBRE_LENGTH,
        max = UsuarioValidations.MAX_NOMBRE_LENGTH,
        message = UsuarioValidations.MSG_NOMBRE_LENGTH
    )
    @Pattern(regexp = UsuarioValidations.PATRON_NOMBRE)
    private String nombre;
}