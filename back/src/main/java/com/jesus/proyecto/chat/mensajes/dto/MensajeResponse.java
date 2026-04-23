package com.jesus.proyecto.chat.mensajes.dto;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jesus.proyecto.chat.mensajes.utils.TipoMensaje;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MensajeResponse {

    private UUID id;

    private UUID usuarioId;

    private UUID chatId;

    private UUID mensajeRespuestaId; // puede ser null

    private String mensaje;

    private String urlArchivo; // puede ser null

    private Instant fechaEnvio;

    private TipoMensaje tipo;

}