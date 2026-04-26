package com.jesus.proyecto.chat.mensajes.dto;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jesus.proyecto.chat.mensajes.utils.TipoMensaje;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MensajeResponse {

    private UUID id;

    private UUID usuarioId;

    private UUID chatId;

    private UUID mensajeRespuestaId; // puede ser null

    private String mensaje;

    // private String urlArchivo; // puede ser null

    private Instant fechaEnvio;

    private TipoMensaje tipo;

}