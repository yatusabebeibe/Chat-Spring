package com.jesus.proyecto.chat.mensajes.service;

import org.springframework.stereotype.Service;

import com.jesus.proyecto.chat.mensajes.entity.Mensaje;
import com.jesus.proyecto.chat.mensajes.repository.MensajeRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class MensajeService {

    private final MensajeRepository mensajeRepository;

    public Mensaje guardar(Mensaje mensaje) {
        return mensajeRepository.save(mensaje);
    }
}
