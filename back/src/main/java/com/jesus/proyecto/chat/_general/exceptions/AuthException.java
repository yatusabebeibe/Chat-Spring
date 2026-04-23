package com.jesus.proyecto.chat._general.exceptions;

import java.util.Map;

public class AuthException extends AC_CustomException {

    public AuthException() {
        super("Autenticación incorrecta",401);
    }

    public AuthException(String mensaje) {
        super(mensaje,401);
    }

    public AuthException(Map<String, String> detalles) {
        super("",401);
        setDetalles(detalles);
    }
}
