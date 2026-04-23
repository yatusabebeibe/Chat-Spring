package com.jesus.proyecto.chat.chats.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.jesus.proyecto.chat.mensajes.dto.MensajeRequest;
import com.jesus.proyecto.chat.mensajes.entity.Mensaje;
import com.jesus.proyecto.chat.mensajes.mapper.MensajeMapper;
import com.jesus.proyecto.chat.mensajes.service.MensajeService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class ChatWsController {

    private final MensajeService mensajeService;
    private final SimpMessagingTemplate messagingTemplate;
    private final MensajeMapper mensajeMapper;

    @MessageMapping("/chat/enviar")
    public void recibirMensaje(@Valid MensajeRequest mensajeRequest) {

        // 1. guardar en BD
        Mensaje guardado = mensajeService.guardar(mensajeRequest);

        // 2. enviar al chat correcto
        messagingTemplate.convertAndSend(
            "/recibir/chat/" + mensajeRequest.getChatId(),
            mensajeMapper.toResponse(guardado)
        );
    }
}