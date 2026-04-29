package com.jesus.proyecto.chat._general.exceptions;

public class UsuarioYaEstaEnGrupoException extends AC_CustomException {

    public UsuarioYaEstaEnGrupoException() {
        super("El usuario ya esta en este grupo", 409);
    }
}