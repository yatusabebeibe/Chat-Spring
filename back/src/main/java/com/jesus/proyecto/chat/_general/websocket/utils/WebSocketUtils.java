package com.jesus.proyecto.chat._general.websocket.utils;

import java.util.List;

import org.springframework.stereotype.Component;

import com.jesus.proyecto.chat._general.ai.service.AIService;
import com.jesus.proyecto.chat.mensajes.dto.CrearMensajeRequest;
import com.jesus.proyecto.chat.mensajes.dto.SessionMessageState;
import com.jesus.proyecto.chat.mensajes.utils.TipoMensaje;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WebSocketUtils {

    private final AIService aiService;


    private List<String> obtenerExtensiones(CrearMensajeRequest req) {
        if (req.getArchivos() == null) return List.of();

        return req.getArchivos().stream()
                .map(a -> {
                    String nombre = a.getNombre();
                    if (nombre == null) return "";
                    int i = nombre.lastIndexOf('.');
                    return (i > 0) ? nombre.substring(i + 1).toLowerCase() : "";
                })
                .toList();
    }

    public TipoMensaje calcularTipoMensaje(SessionMessageState state) {
        if (state.getExpectedFiles() == 0) {
            return TipoMensaje.TEXTO;
        }

        List<String> extensiones = obtenerExtensiones(state.getRequest());

        if (extensiones.isEmpty()) {
            return TipoMensaje.OTROS;
        }

        return aiService.detectarTipoMensaje(extensiones);
    }
}
