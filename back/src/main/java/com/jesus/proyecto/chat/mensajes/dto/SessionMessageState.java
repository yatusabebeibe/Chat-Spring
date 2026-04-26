package com.jesus.proyecto.chat.mensajes.dto;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class SessionMessageState {

    private UUID messageId;

    private UUID usuarioId;

    private UUID chatId;

    private MensajeRequest request;

    private Map<Integer, byte[]> archivos = new ConcurrentHashMap<>();

    private int expectedFiles;

    private int receivedFiles = 0;
}