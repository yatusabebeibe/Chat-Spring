package com.jesus.proyecto.chat._general.handler;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.jesus.proyecto.chat._general.exceptions.MyAuthException;
import com.jesus.proyecto.chat.auth.service.JwtService;
import com.jesus.proyecto.chat.chats.entity.Chat;
import com.jesus.proyecto.chat.mensajes.dto.MensajeRequest;
import com.jesus.proyecto.chat.mensajes.entity.Mensaje;
import com.jesus.proyecto.chat.mensajes.mapper.MensajeMapper;
import com.jesus.proyecto.chat.mensajes.service.MensajeService;
import com.jesus.proyecto.chat.relacionUsuarioChat.service.UsuarioChatService;
import com.jesus.proyecto.chat.usuarios.service.UsuarioService;

import lombok.AllArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Component
@AllArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final UsuarioChatService usuarioChatService;
    private final UsuarioService usuarioService;
    private final JwtService jwtService;
    private final MensajeService mensajeService;
    private final MensajeMapper mensajeMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<UUID, Set<WebSocketSession>> rooms = new ConcurrentHashMap<>();



    @Override
    public void afterConnectionEstablished(WebSocketSession session) {

        UUID userId = obtenerUsuario(session);

        List<Chat> chats = usuarioChatService.listarChatsPorUsuario(userId);

        for (Chat chat : chats) {
            rooms.computeIfAbsent(chat.getId(), k -> ConcurrentHashMap.newKeySet());
            rooms.get(chat.getId()).add(session);
        }

        System.out.println("Cliente conectado: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        rooms.values().forEach(set -> set.remove(session));
        rooms.entrySet().removeIf(entry -> entry.getValue().isEmpty());
        System.out.println("Cliente desconectado: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        MensajeRequest req = objectMapper.readValue(message.getPayload(), MensajeRequest.class);

        UUID userId = obtenerUsuario(session);
        UUID roomId = req.getChatId();

        Mensaje mensaje = mensajeMapper.toEntity(req);
        mensaje.setUsuario(usuarioService.buscarPorId(userId));
        Mensaje guardado = mensajeService.guardar(mensaje);

        var response = mensajeMapper.toResponse(guardado);
        String json = objectMapper.writeValueAsString(response);

        rooms.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet());

        Set<WebSocketSession> roomSessions = rooms.get(roomId);
        roomSessions.add(session);

        for (WebSocketSession sesion : roomSessions) {
            if (sesion.isOpen()) {
                sesion.sendMessage(new TextMessage(json));
            }
        }
    }

    private UUID obtenerUsuario(WebSocketSession session) {
        String token = (String) session.getAttributes().get("jwt");

        if (token == null) return null;

        return jwtService.extraerUsuarioId(token);
    }
}
