package com.jesus.proyecto.chat.archivoMensaje.entity;

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
public class I_ArchivoMensajeId implements Serializable {

    private UUID msgId;
    private int indice;
}