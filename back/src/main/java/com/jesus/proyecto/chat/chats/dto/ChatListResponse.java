package com.jesus.proyecto.chat.chats.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jesus.proyecto.chat.mensajes.dto.MensajeResponse;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatListResponse extends ChatResponse {

    private MensajeResponse ultimoMensaje; // para preview del último mensaje en la interfaz y su fecha y hora

    private String avatarConversacion;

}
