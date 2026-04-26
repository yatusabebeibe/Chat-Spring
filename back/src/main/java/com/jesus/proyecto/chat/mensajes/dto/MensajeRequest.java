package com.jesus.proyecto.chat.mensajes.dto;

import java.util.List;
import java.util.UUID;

import com.jesus.proyecto.chat.archivoMensaje.dto.ArchivoRequest;
import com.jesus.proyecto.chat.mensajes.utils.TipoMensaje;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
public class MensajeRequest {

    @NotNull(message = "El chat es obligatorio")
    private UUID chatId;

    @Size(max = 2000, message = "El mensaje es demasiado largo")
    private String mensaje;


    // Lo completo yo
    @Setter
    private TipoMensaje tipo;


    // opcionales

    private UUID mensajeRespuestaId;

    private List<ArchivoRequest> archivos;
}