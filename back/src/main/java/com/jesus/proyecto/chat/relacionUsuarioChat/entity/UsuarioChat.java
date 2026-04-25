package com.jesus.proyecto.chat.relacionUsuarioChat.entity;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import com.jesus.proyecto.chat.chats.entity.Chat;
import com.jesus.proyecto.chat.mensajes.entity.Mensaje;
import com.jesus.proyecto.chat.usuarios.entity.Usuario;
import com.jesus.proyecto.chat.usuarios.utils.RolEnChat;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "usuario_chat")
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class UsuarioChat {


    @EmbeddedId
    private I_UsuarioChatId id;

    @Column(nullable = false, columnDefinition = "DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP")
    @CreationTimestamp
    private Instant fechaUnion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RolEnChat rol;

    @ManyToOne
    @MapsId("idUsuario")
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @MapsId("idChat")
    @JoinColumn(name = "id_chat")
    private Chat chat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ultimo_mensaje_leido")
    private Mensaje ultimoMensajeLeido;


    public static UsuarioChat crear(Usuario usr, Chat chat, RolEnChat rol) {
        UsuarioChat usrChat = new UsuarioChat();
        usrChat.setUsuario(usr);
        usrChat.setChat(chat);
        usrChat.setRol(rol);
        usrChat.setId(new I_UsuarioChatId(usr.getId(), chat.getId()));
        return usrChat;
    }

    public void marcarComoLeido(Mensaje mensaje) {
        this.ultimoMensajeLeido = mensaje;
    }
}
