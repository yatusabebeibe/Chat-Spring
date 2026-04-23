package com.jesus.proyecto.chat._general.exceptions;

import java.util.Map;

import lombok.Getter;

@Getter
public abstract class AC_CustomException extends RuntimeException {

    protected int codigoError = 500;
    protected String mensaje = "Error del servidor";
    protected Map<String, String> detalles = null;

    protected AC_CustomException(String mensaje) {
        this.mensaje = mensaje;
    }

    protected AC_CustomException(String mensaje, int codigoError) {
        this.codigoError = codigoError;
        this.mensaje = mensaje;
    }

    protected void setDetalles(Map<String, String> detalles) {
        this.detalles = detalles;

        if (detalles != null && !detalles.isEmpty()) {
            this.mensaje = "";
        }
    }
}
