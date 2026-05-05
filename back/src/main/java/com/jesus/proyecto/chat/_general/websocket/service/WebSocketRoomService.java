package com.jesus.proyecto.chat._general.websocket.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.jesus.proyecto.chat.chats.entity.Chat;
import com.jesus.proyecto.chat.relacionUsuarioChat.service.UsuarioChatService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class WebSocketRoomService {

    private final Map<UUID, Set<WebSocketSession>> rooms = new ConcurrentHashMap<>();
    private final Map<UUID, Set<UUID>> sessionChats = new ConcurrentHashMap<>();

    private final UsuarioChatService usuarioChatService;

    // -------------------------
    // CONEXIÓN
    // -------------------------
    public void handleConnection(WebSocketSession session) {
        try {
            UUID userId = (UUID) session.getAttributes().get("userId");
            if (userId == null) return;

            UUID sessionId = UUID.fromString(session.getId());

            List<Chat> chats = usuarioChatService.listarChatsPorUsuario(userId);

            Set<UUID> chatIds = new HashSet<>();

            for (Chat chat : chats) {
                UUID chatId = chat.getId();

                rooms.computeIfAbsent(chatId, k -> ConcurrentHashMap.newKeySet())
                        .add(session);

                chatIds.add(chatId);
            }

            sessionChats.put(sessionId, chatIds);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -------------------------
    // DESCONEXIÓN
    // -------------------------
    public void handleDisconnection(WebSocketSession session) {
        try {
            UUID sessionId = UUID.fromString(session.getId());

            Set<UUID> chats = sessionChats.remove(sessionId);
            if (chats == null) return;

            for (UUID chatId : chats) {
                Set<WebSocketSession> sessions = rooms.get(chatId);
                if (sessions != null) {
                    sessions.remove(session);

                    if (sessions.isEmpty()) {
                        rooms.remove(chatId);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -------------------------
    // BROADCAST
    // -------------------------
    public void broadcast(UUID chatId, String msg) {
        Set<WebSocketSession> sessions = rooms.get(chatId);
        if (sessions == null || sessions.isEmpty()) return;

        // Eliminar sesión del room si el cliente ya no esta abierto (opcional para evitar errores si no se desconectó bien)
        // Pero mejor es filtrar isOpen() que hace lo mismo

        for (WebSocketSession s : new HashSet<>(sessions)) {
            if (!s.isOpen()) {
                sessions.remove(s);
                continue;
            }

            try {
                s.sendMessage(new TextMessage(msg));
            } catch (Exception e) {
                sessions.remove(s);
            }
        }
    }
}