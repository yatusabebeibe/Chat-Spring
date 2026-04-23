package com.jesus.proyecto.chat.relacionUsuarioChat.entity;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class I_UsuarioChatId implements Serializable {

    private UUID idUsuario;
    private UUID idChat;
}