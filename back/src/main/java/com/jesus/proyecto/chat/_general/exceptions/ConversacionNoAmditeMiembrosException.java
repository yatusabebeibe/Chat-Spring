package com.jesus.proyecto.chat._general.exceptions;

import java.util.Map;

public class ConversacionNoAmditeMiembrosException extends AC_CustomException {

    public ConversacionNoAmditeMiembrosException() {
        super("", 400);
        setDetalles(Map.of("conversacion", "No puedes añadir otros miembros a una conversacion"));
    }
}