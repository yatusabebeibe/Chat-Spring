package com.jesus.proyecto.chat.mensajes.entity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.jesus.proyecto.chat._general.utils.UUIDv7;
import com.jesus.proyecto.chat.archivoMensaje.entity.ArchivoMensaje;
import com.jesus.proyecto.chat.chats.entity.Chat;
import com.jesus.proyecto.chat.mensajes.utils.TipoMensaje;
import com.jesus.proyecto.chat.usuarios.entity.Usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "mensajes")
@Getter
@Setter
@NoArgsConstructor
public class Mensaje {

    @Id
    // @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @PrePersist // codigo que se ejecuta antes de que se ejecute el insert en la DB
    private void prePersist() {
        if (id == null) {
            id = UUIDv7.randomUUID();
        }
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mensaje_respuesta", nullable = true)
    private Mensaje mensajeRespuesta;

    // QUIEN ENVÍA EL MENSAJE
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    // EN QUÉ CHAT SE ENVÍA
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_chat", nullable = false)
    private Chat chat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMensaje tipo;

    @Column(columnDefinition = "TEXT")
    private String mensaje;

    @Column(nullable = false, columnDefinition = "DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP")
    @CreationTimestamp
    private Instant fechaEnvio = Instant.now();

    @Column(nullable = true, columnDefinition = "DATETIME(3)")
    private Instant fechaEdicion;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean eliminado = false;

    @OneToMany(mappedBy = "mensaje", fetch = FetchType.LAZY)
    private List<ArchivoMensaje> archivos;

}