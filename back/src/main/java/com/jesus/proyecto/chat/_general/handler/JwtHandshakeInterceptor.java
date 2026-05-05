package com.jesus.proyecto.chat._general.handler;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.jesus.proyecto.chat.auth.service.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtService jwtService;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {
        HttpServletRequest servletRequest =
                ((ServletServerHttpRequest) request).getServletRequest();

        String cookies = servletRequest.getHeader("cookie");

        if (cookies == null || cookies.isBlank()) {
            return false;
        }

        String token = null;

        for (String cookie : cookies.split(";")) {
            String[] parts = cookie.trim().split("=");

            if (parts.length == 2 && "accessToken".equals(parts[0])) {
                token = parts[1];
                break;
            }
        }

        if (token == null || token.isBlank()) {
            return false;
        }

        try {
            UUID userId = jwtService.extraerUsuarioId(token);

            if (userId == null) {
                return false;
            }

            attributes.put("userId", userId);
            return true;

        } catch (Exception e) {
            System.out.println("JWT inválido");
            return false;
        }
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception) {}
}