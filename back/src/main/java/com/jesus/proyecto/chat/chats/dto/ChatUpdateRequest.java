package com.jesus.proyecto.chat.chats.dto;

import java.util.UUID;

import com.jesus.proyecto.chat.chats.utils.ChatValidations;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ChatUpdateRequest {

    @NotNull(message = "Se necesita el id")
    private UUID id;

    @NotBlank(message = "Es obligatorio poner nombre al grupo")
    @Size(
        min = ChatValidations.MIN_NOMBRE_LENGTH,
        max = ChatValidations.MAX_NOMBRE_LENGTH,
        message = ChatValidations.MSG_NOMBRE_LENGTH
    )
    private String nombre;
}
