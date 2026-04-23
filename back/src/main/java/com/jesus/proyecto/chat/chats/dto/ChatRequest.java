package com.jesus.proyecto.chat.chats.dto;

import java.util.UUID;

import com.jesus.proyecto.chat.chats.utils.ChatValidations;
import com.jesus.proyecto.chat.chats.utils.TipoChat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ChatRequest {

    @NotBlank(message = "Es obligatorio poner nombre al grupo")
    @Size(
        min = ChatValidations.MIN_NOMBRE_LENGTH,
        max = ChatValidations.MAX_NOMBRE_LENGTH,
        message = ChatValidations.MSG_NOMBRE_LENGTH
    )
    @Pattern(regexp = ChatValidations.PATRON_NOMBRE)
    private String nombre;

    @NotNull(message = "Tiene que tener un tipo")
    private TipoChat tipo; // TipoChat.CONVERSACION o TipoChat.GRUPO

    @NotNull(message = "No hay usuario creador")
    private UUID idCreador; // el que inicia la conversacion

    private UUID idParticipante; // si es conversacion, la persona a la que agrega al crear
}
