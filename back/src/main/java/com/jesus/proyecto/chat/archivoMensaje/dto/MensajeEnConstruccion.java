package com.jesus.proyecto.chat.archivoMensaje.dto;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jesus.proyecto.chat.mensajes.dto.CrearMensajeRequest;

import lombok.Getter;

@Getter
public class MensajeEnConstruccion {

    private CrearMensajeRequest request;

    private Map<Integer, byte[]> archivos = new ConcurrentHashMap<>();

    private int esperados;

    private int recibidos = 0;
}