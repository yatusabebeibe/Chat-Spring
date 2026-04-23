package com.jesus.proyecto.chat._general.exceptions;

public class UsuarioYaExisteException extends AC_CustomException {

    public UsuarioYaExisteException() {
        super("Este nombre de usuario ya está en uso", 409);
    }
}
