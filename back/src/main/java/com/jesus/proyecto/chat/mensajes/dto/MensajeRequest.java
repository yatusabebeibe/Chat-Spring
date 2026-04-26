package com.jesus.proyecto.chat.mensajes.dto;

import java.util.UUID;

import com.jesus.proyecto.chat.mensajes.utils.SentidoMensaje;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MensajeRequest {

    @NotNull(message = "Es obligatorio introducir un chatId")
    private UUID chatId;

    UUID msgId;

    private Integer limite;

    private SentidoMensaje sentido;
}