package com.jesus.proyecto.chat.archivoMensaje.entity;

import com.jesus.proyecto.chat.mensajes.entity.Mensaje;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "archivo_mensaje")
@Getter
@Setter
@NoArgsConstructor
public class ArchivoMensaje {

    @EmbeddedId
    private I_ArchivoMensajeId id;

    @MapsId("msgId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "msg_id", nullable = false)
    private Mensaje mensaje;

    @Column(nullable = false)
    private String url;
}