package com.jesus.proyecto.chat._general.handler;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import jakarta.servlet.http.HttpServletRequest;

public class JwtHandshakeInterceptor implements HandshakeInterceptor {

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

        if (cookies != null) {
            for (String cookie : cookies.split(";")) {
                String[] parts = cookie.trim().split("=");

                if (parts.length == 2 && parts[0].equals("jwt")) {
                    attributes.put("jwt", parts[1]);
                }
            }
        }

        return true;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception) {}
}