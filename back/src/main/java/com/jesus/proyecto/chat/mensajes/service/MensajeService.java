package com.jesus.proyecto.chat.mensajes.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.jesus.proyecto.chat._general.exceptions.DatosRequeridosException;
import com.jesus.proyecto.chat.mensajes.dto.MensajeResponse;
import com.jesus.proyecto.chat.mensajes.entity.Mensaje;
import com.jesus.proyecto.chat.mensajes.mapper.MensajeMapper;
import com.jesus.proyecto.chat.mensajes.repository.MensajeRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class MensajeService {

    private final MensajeMapper mensajeMapper;
    private final MensajeRepository mensajeRepository;

    public Mensaje guardar(Mensaje mensaje) {
        return mensajeRepository.save(mensaje);
    }

    public List<MensajeResponse> obtenerMensajesChat(UUID chatId, UUID msgId) {
        if (chatId == null) throw new DatosRequeridosException();

        List<Mensaje> mensajes;

        if (msgId == null) { 
            // obtiene los ultimos 25 mensajes del chat
            mensajes = mensajeRepository.findTop25ByChatIdAndEliminadoFalseOrderByIdDesc(chatId);
        } else {
            // obtiene los ultimos 25 mensajes del chat a partir de X msg
            mensajes = mensajeRepository.findTop25ByChatIdAndEliminadoFalseAndIdLessThanOrderByIdDesc(chatId, msgId);
        }

        return mensajes.stream()
                .map(msg -> mensajeMapper.toResponse(msg))
                .toList();
    }
}
