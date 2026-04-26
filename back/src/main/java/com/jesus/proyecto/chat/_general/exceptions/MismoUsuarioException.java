package com.jesus.proyecto.chat._general.exceptions;

public class MismoUsuarioException extends AC_CustomException {

    public MismoUsuarioException() {
        super("No puedes añadirte a ti mismo a una conversacion", 400);
    }
}