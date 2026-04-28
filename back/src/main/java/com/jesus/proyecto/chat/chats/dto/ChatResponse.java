package com.jesus.proyecto.chat.chats.dto;

import java.time.Instant;
import java.util.UUID;

import com.jesus.proyecto.chat.chats.utils.TipoChat;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ChatResponse {

    private UUID id;

    private String nombre; // puede ser null para conversaciones 1a1

    private TipoChat tipo; // CONVERSACION (1a1) o GRUPO

    private UUID idCreador;

    private String extensionImagen; // puede ser null

    private Instant fechaCreacion;
}
