package com.jesus.proyecto.chat.usuarios.dto;

import com.jesus.proyecto.chat.usuarios.utils.RolEnChat;

import lombok.Data;

@Data
public class UsuarioChatResponse extends UsuarioResponse {

    private RolEnChat rol;
}
