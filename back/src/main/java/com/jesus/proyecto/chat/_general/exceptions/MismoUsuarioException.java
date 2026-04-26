package com.jesus.proyecto.chat._general.exceptions;

import java.util.Map;

public class MismoUsuarioException extends AC_CustomException {

    public MismoUsuarioException() {
        super("", 400);
        setDetalles(Map.of("conversacion", "No puedes añadirte a ti mismo a una conversacion"));
    }
}