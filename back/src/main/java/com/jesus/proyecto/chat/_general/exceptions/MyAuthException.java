package com.jesus.proyecto.chat._general.exceptions;

import java.util.Map;

public class MyAuthException extends AC_CustomException {

    public MyAuthException() {
        super("Autenticación incorrecta",401);
    }

    public MyAuthException(String mensaje) {
        super(mensaje,401);
    }

    public MyAuthException(Map<String, String> detalles) {
        super("",401);
        setDetalles(detalles);
    }
}
