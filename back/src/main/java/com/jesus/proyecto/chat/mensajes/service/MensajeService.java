package com.jesus.proyecto.chat.mensajes.service;

import org.springframework.stereotype.Service;

import com.jesus.proyecto.chat.chats.repository.ChatRepository;
import com.jesus.proyecto.chat.mensajes.dto.MensajeRequest;
import com.jesus.proyecto.chat.mensajes.entity.Mensaje;
import com.jesus.proyecto.chat.mensajes.mapper.MensajeMapper;
import com.jesus.proyecto.chat.mensajes.repository.MensajeRepository;
import com.jesus.proyecto.chat.usuarios.repository.UsuarioRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class MensajeService {

    private final MensajeRepository mensajeRepository;
    private final ChatRepository chatRepository;
    private final UsuarioRepository usuarioRepository;
    private final MensajeMapper mensajeMapper;


    public Mensaje guardar(MensajeRequest request) {

        Mensaje mensaje = mensajeMapper.toEntity(request);

        mensaje.setChat(chatRepository.findById(request.getChatId()).orElseThrow());

        mensaje.setUsuario(usuarioRepository.findById(request.getUsuarioId()).orElseThrow());

        System.out.println("\n\n---");
        System.out.println(request.getMensajeRespuestaId());
        System.out.println("---\n\n");
        if (request.getMensajeRespuestaId() != null) {
            System.out.println("entra");
            Mensaje respuesta = mensajeRepository.getReferenceById(request.getMensajeRespuestaId());
            mensaje.setMensajeRespuesta(respuesta);
        }
        else {
            mensaje.setMensajeRespuesta(null);
        }

        return mensajeRepository.save(mensaje);
    }
}
