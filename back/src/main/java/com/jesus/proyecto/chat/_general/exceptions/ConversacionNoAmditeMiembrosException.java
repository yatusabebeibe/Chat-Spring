package com.jesus.proyecto.chat._general.exceptions;

public class ConversacionNoAmditeMiembrosException extends AC_CustomException {

    public ConversacionNoAmditeMiembrosException() {
        super("No puedes añadir otros miembros a una conversacion", 400);
    }
}