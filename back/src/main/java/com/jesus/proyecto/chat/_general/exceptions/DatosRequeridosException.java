package com.jesus.proyecto.chat._general.exceptions;

public class DatosRequeridosException extends AC_CustomException {

    public DatosRequeridosException() {
        super("Faltan datos obligatorios", 400);
    }
}