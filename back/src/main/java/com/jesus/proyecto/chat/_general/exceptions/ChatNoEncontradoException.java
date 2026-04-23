package com.jesus.proyecto.chat._general.exceptions;

public class ChatNoEncontradoException extends AC_CustomException {

    public ChatNoEncontradoException() {
        super("El chat no se ha encontrado", 404);
    }
}
