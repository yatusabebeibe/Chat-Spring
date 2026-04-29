package com.jesus.proyecto.chat.mensajes.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.jesus.proyecto.chat._general.exceptions.DatosRequeridosException;
import com.jesus.proyecto.chat._general.utils.Paginacion;
import com.jesus.proyecto.chat.mensajes.dto.MensajeRequest;
import com.jesus.proyecto.chat.mensajes.dto.MensajeResponse;
import com.jesus.proyecto.chat.mensajes.entity.Mensaje;
import com.jesus.proyecto.chat.mensajes.mapper.MensajeMapper;
import com.jesus.proyecto.chat.mensajes.repository.MensajeRepository;
import com.jesus.proyecto.chat.mensajes.utils.SentidoMensaje;

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

    public List<MensajeResponse> obtenerMensajesChat(MensajeRequest req) {
        if (req.getChatId() == null) throw new DatosRequeridosException();

        System.out.println("---------------");

        int limite = Paginacion.validarLimite(req.getLimite());

        List<Mensaje> mensajes;

        if (req.getMsgId() == null) {
            mensajes = getInitial(req.getChatId(), limite);
        } else {
            if (req.getSentido() == null) {
                mensajes = List.of(obtenerMensaje(req.getMsgId()));
            } else {
                if (req.getSentido() == SentidoMensaje.ANT) {
                    mensajes = getOlder(req.getChatId(), req.getMsgId(), limite);
                } else {
                    mensajes = getNewer(req.getChatId(), req.getMsgId(), limite);
                }
            }
        }

        System.out.println(mensajes.toString());
        System.out.println("---------------");

        return mensajes.stream()
                .map(mensajeMapper::toResponse)
                .toList();
    }

    public List<Mensaje> getInitial(UUID chatId, int size) {
        Sort orden = Sort.by(Direction.DESC, "id");
        return mensajeRepository.findByChatIdAndEliminadoFalse(
            chatId,
            PageRequest.of(0, size, orden)
        ).reversed();
    }

    public List<Mensaje> getOlder(UUID chatId, UUID cursor, int size) {
        return mensajeRepository.findByChatIdAndEliminadoFalseAndIdLessThanOrderByIdDesc(
            chatId,
            cursor,
            PageRequest.of(0, size)
        ).reversed();
    }

    public List<Mensaje> getNewer(UUID chatId, UUID cursor, int size) {
        return mensajeRepository.findByChatIdAndEliminadoFalseAndIdGreaterThanOrderByIdAsc(
            chatId,
            cursor,
            PageRequest.of(0, size)
        );
    }

    public Mensaje obtenerMensaje(UUID id) {
        return mensajeRepository.findById(id).orElse(null);
    }
}
