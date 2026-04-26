package com.jesus.proyecto.chat._general.websocket.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import com.jesus.proyecto.chat.mensajes.dto.SessionMessageState;

@Service
public class WebSocketSessionService {

    private final Map<WebSocketSession, SessionMessageState> sessionsState = new ConcurrentHashMap<>();
    private final Map<WebSocketSession, UUID> sessionChatIds = new ConcurrentHashMap<>();

    public void saveState(WebSocketSession session, SessionMessageState state) {
        sessionsState.put(session, state);
        sessionChatIds.put(session, state.getChatId());
    }

    public SessionMessageState getState(WebSocketSession session) {
        return sessionsState.get(session);
    }

    public UUID getChatId(WebSocketSession session) {
        return sessionChatIds.get(session);
    }

    public void remove(WebSocketSession session) {
        sessionsState.remove(session);
        sessionChatIds.remove(session);
    }
}