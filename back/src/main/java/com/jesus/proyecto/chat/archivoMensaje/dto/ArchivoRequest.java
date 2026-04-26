package com.jesus.proyecto.chat.archivoMensaje.dto;

import lombok.Getter;

@Getter
public class ArchivoRequest {

    private String nombre;

    private String tipo;

    private int indice; // orden del archivo
}