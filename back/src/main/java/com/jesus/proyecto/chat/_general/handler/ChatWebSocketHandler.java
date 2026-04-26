package com.jesus.proyecto.chat._general.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.jesus.proyecto.chat._general.websocket.service.WebSocketFileService;
import com.jesus.proyecto.chat._general.websocket.service.WebSocketMessageService;
import com.jesus.proyecto.chat._general.websocket.service.WebSocketRoomService;
import com.jesus.proyecto.chat._general.websocket.service.WebSocketSessionService;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final WebSocketFileService fileService;
    private final WebSocketRoomService roomService;
    private final WebSocketMessageService messageService;
    private final WebSocketSessionService sessionService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        roomService.handleConnection(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        roomService.handleDisconnection(session);
        sessionService.remove(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        messageService.handleText(session, message.getPayload());
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        fileService.handleBinary(session, message);
    }
}
