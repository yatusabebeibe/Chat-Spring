package com.jesus.proyecto.chat.chats.entity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.jesus.proyecto.chat.chats.utils.ChatValidations;
import com.jesus.proyecto.chat.chats.utils.TipoChat;
import com.jesus.proyecto.chat.mensajes.entity.Mensaje;
import com.jesus.proyecto.chat.relacionUsuarioChat.entity.UsuarioChat;
import com.jesus.proyecto.chat.usuarios.entity.Usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "chats")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = ChatValidations.MAX_NOMBRE_LENGTH)
    @Pattern(regexp = ChatValidations.PATRON_NOMBRE)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    
    private TipoChat tipo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_creador", nullable = false)
    private Usuario creador;

    @Column(nullable = false, updatable = false, columnDefinition = "DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP")
    @CreationTimestamp
    private Instant fechaCreacion;

    @OneToMany(mappedBy = "chat", fetch = FetchType.LAZY)
    private List<UsuarioChat> miembros;


    @OneToMany(mappedBy = "chat", fetch = FetchType.LAZY)
    private List<Mensaje> mensajes;
}
