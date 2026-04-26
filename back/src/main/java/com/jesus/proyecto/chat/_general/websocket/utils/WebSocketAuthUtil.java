package com.jesus.proyecto.chat._general.websocket.utils;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import com.jesus.proyecto.chat.auth.service.JwtService;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class WebSocketAuthUtil {

    private final JwtService jwtService;

    public UUID obtenerUsuario(WebSocketSession session) {
        List<String> cookies = session.getHandshakeHeaders().get("Cookie");

        if (cookies == null || cookies.isEmpty()) {
            return null;
        }

        String cookieHeader = cookies.get(0);

        // Buscar accessToken dentro del string
        String token = Arrays.stream(cookieHeader.split(";"))
                .map(String::trim)
                .filter(c -> c.startsWith("accessToken="))
                .map(c -> c.substring("accessToken=".length()))
                .findFirst()
                .orElse(null);

        if (token == null) {
            return null;
        }

        try {
            return jwtService.extraerUsuarioId(token);
        } catch (Exception e) {
            System.err.println("Error al extraer usuario del token: " + e.getMessage());
            return null;
        }
    }
}