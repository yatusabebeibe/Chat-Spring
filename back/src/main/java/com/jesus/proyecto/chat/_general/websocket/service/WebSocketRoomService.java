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

import com.jesus.proyecto.chat._general.websocket.utils.WebSocketAuthUtil;
import com.jesus.proyecto.chat.chats.entity.Chat;
import com.jesus.proyecto.chat.relacionUsuarioChat.service.UsuarioChatService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class WebSocketRoomService {

    private final Map<UUID, Set<WebSocketSession>> rooms = new ConcurrentHashMap<>();

    private final UsuarioChatService usuarioChatService;
    private final WebSocketAuthUtil authUtil;
    private final WebSocketSessionService sessionService;

    // -------------------------
    // CONEXIÓN
    // -------------------------
    public void handleConnection(WebSocketSession session) {
        try {
            UUID userId = authUtil.obtenerUsuario(session);

            if (userId == null) {
                return;
            }

            List<Chat> chats = usuarioChatService.listarChatsPorUsuario(userId);

            for (Chat chat : chats) {
                Set<WebSocketSession> sessions =
                        rooms.computeIfAbsent(chat.getId(), k -> ConcurrentHashMap.newKeySet());

                sessions.add(session);

                session.getAttributes().put("chatId", chat.getId());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -------------------------
    // DESCONEXIÓN
    // -------------------------
    public void handleDisconnection(WebSocketSession session) {
        try {
            UUID chatId = sessionService.getChatId(session);

            if (chatId != null && rooms.containsKey(chatId)) {
                Set<WebSocketSession> sessions = rooms.get(chatId);

                if (sessions != null) {
                    sessions.remove(session);
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
            if (s.isOpen()) {
                try {
                    s.sendMessage(new TextMessage(msg));
                } catch (Exception e) {
                    // Si la conexión se rompió, no podemos enviarle mensaje. 
                    // Se encargará de limpiarse en afterConnectionClosed.
                    // No necesitamos hacer nada aquí, pero loguear:
                    System.out.println("Error enviando broadcast: " + e.getMessage());
                }
            } else {
                sessions.remove(s);
            }
        }
    }
}