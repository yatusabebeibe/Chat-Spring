package com.jesus.proyecto.chat._general.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.jesus.proyecto.chat._general.exceptions.AC_CustomException;
import com.jesus.proyecto.chat._general.exceptions.AuthException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AC_CustomException.class)
    public ResponseEntity<Map<String, String>> handleCustomException(AC_CustomException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", e.getMensaje());
        return ResponseEntity.status(e.getCodigoError()).body(response);
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<Map<String, Object>> handleAuthException(AuthException e) {
        
        Map<String, Object> response = new HashMap<>();

        // Si hay detalles de validación específicos
        if (e.getDetalles() != null && !e.getDetalles().isEmpty()) {
            response.put("error", e.getDetalles());
            return ResponseEntity.status(e.getCodigoError()).body(response);
        } else {
            // Error único
            response.put("error", e.getMensaje());
            return ResponseEntity.status(e.getCodigoError()).body(response);
        }
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException e) {

        Map<String, String> fieldErrors = new HashMap<>();

        e.getBindingResult().getFieldErrors().forEach(error -> {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        });

        Map<String, Object> response = new HashMap<>();
        response.put("error", fieldErrors);

        return ResponseEntity.badRequest().body(response);
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Violacion de integridad en base de datos");
        response.put("message", e.getMostSpecificCause().getMessage());

        return ResponseEntity.status(409).body(response);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception e) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Error interno");
        response.put("message", e.getMessage());

        return ResponseEntity.status(500).body(response);
    }
}