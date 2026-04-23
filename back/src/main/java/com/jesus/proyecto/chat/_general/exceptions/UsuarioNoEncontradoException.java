package com.jesus.proyecto.chat._general.exceptions;

public class UsuarioNoEncontradoException extends AC_CustomException {

    public UsuarioNoEncontradoException() {
        super("No se ha encontrado este usuario", 404);
    }
}
