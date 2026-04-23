package com.jesus.proyecto.chat._general.exceptions;

public class MensajeNoEncontradoException extends AC_CustomException {

    public MensajeNoEncontradoException() {
        super("El mensaje no se ha encontrado", 404);
    }
}
