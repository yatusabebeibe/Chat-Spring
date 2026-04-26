package com.jesus.proyecto.chat.mensajes.dto;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Data;

@Data
public class SessionMessageState {

    private UUID messageId;

    private UUID usuarioId;

    private UUID chatId;

    private CrearMensajeRequest request;

    private Map<Integer, byte[]> archivos = new ConcurrentHashMap<>();

    private Map<Integer, String> rutasArchivos;

    private int expectedFiles;

    private int receivedFiles = 0;
}