package com.jesus.proyecto.chat.chats.dto;

import com.jesus.proyecto.chat.mensajes.dto.MensajeResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatListResponse extends ChatResponse {

    private MensajeResponse ultimoMensaje; // para preview del último mensaje en la interfaz y su fecha y hora

}
