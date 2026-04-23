package com.jesus.proyecto.chat.mensajes.dto;

import java.util.UUID;

import com.jesus.proyecto.chat.mensajes.utils.TipoMensaje;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class MensajeRequest {

    @NotNull(message = "El chat es obligatorio")
    private UUID chatId;

    @NotNull(message = "El usuario que envía es obligatorio")
    private UUID usuarioId;

    @NotNull(message = "El tipo de mensaje es obligatorio")
    private TipoMensaje tipo;

    @NotBlank(message = "El mensaje no puede estar vacío")
    @Size(max = 2000, message = "El mensaje es demasiado largo")
    private String mensaje;


    // opcionales

    private String urlArchivo;

    private UUID mensajeRespuestaId;
}