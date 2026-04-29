package com.jesus.proyecto.chat._general.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.jesus.proyecto.chat._general.exceptions.AC_CustomException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AC_CustomException.class)
    public ResponseEntity<Map<String, Object>> handleCustomException(AC_CustomException e) {

        Map<String, Object> response = new HashMap<>();

        // Si hay detalles de validación específicos
        if (e.getDetalles() != null && !e.getDetalles().isEmpty()) {
            response.put("error", e.getDetalles());
        } else {
            // Error único
            response.put("error", e.getMensaje());
        }
        return ResponseEntity.status(e.getCodigoError()).body(response);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception e) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Error interno");
        response.put("message", e.getMessage());

        return ResponseEntity.status(500).body(response);
    }
}