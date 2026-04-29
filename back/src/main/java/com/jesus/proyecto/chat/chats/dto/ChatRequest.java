package com.jesus.proyecto.chat.chats.dto;

import java.util.UUID;

import com.jesus.proyecto.chat.chats.utils.ChatValidations;
import com.jesus.proyecto.chat.chats.utils.TipoChat;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ChatRequest {

    @Size(
        min = ChatValidations.MIN_NOMBRE_LENGTH,
        max = ChatValidations.MAX_NOMBRE_LENGTH,
        message = ChatValidations.MSG_NOMBRE_LENGTH
    )
    @Pattern(regexp = ChatValidations.PATRON_NOMBRE)
    private String nombre;

    @NotNull(message = "Tiene que tener un tipo")
    private TipoChat tipo; // TipoChat.CONVERSACION o TipoChat.GRUPO

    private UUID idParticipante; // si es conversacion, la persona a la que agrega al crear
}
