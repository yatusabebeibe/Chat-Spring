package com.jesus.proyecto.chat.relacionUsuarioChat.service;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jesus.proyecto.chat._general.exceptions.ChatNoEncontradoException;
import com.jesus.proyecto.chat._general.exceptions.MismoUsuarioException;
import com.jesus.proyecto.chat._general.exceptions.UsuarioNoEncontradoException;
import com.jesus.proyecto.chat._general.exceptions.UsuarioYaEstaEnGrupoException;
import com.jesus.proyecto.chat.chats.entity.Chat;
import com.jesus.proyecto.chat.chats.repository.ChatRepository;
import com.jesus.proyecto.chat.chats.utils.TipoChat;
import com.jesus.proyecto.chat.relacionUsuarioChat.entity.I_UsuarioChatId;
import com.jesus.proyecto.chat.relacionUsuarioChat.entity.UsuarioChat;
import com.jesus.proyecto.chat.relacionUsuarioChat.repository.UsuarioChatRepository;
import com.jesus.proyecto.chat.usuarios.entity.Usuario;
import com.jesus.proyecto.chat.usuarios.repository.UsuarioRepository;
import com.jesus.proyecto.chat.usuarios.utils.RolEnChat;

import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class UsuarioChatService {

    private final UsuarioChatRepository usuarioChatRepository;
    private final ChatRepository chatRepository;
    private final UsuarioRepository usuarioRepository;

    public void agregarUsuario(Usuario usr, Chat chat, RolEnChat rol) {

        if (chat.getTipo() != TipoChat.GRUPO) {
            long cantidad = usuarioChatRepository.contarUsuariosEnChat(chat.getId());
            if (cantidad >= 2) {
                throw new MismoUsuarioException();
            }
        }

        if (usuarioChatRepository.existsById_IdUsuarioAndId_IdChat(usr.getId(), chat.getId())) {
            throw new UsuarioYaEstaEnGrupoException();
        }

        UsuarioChat usrChat = UsuarioChat.crear(usr, chat, rol);
        usuarioChatRepository.save(usrChat);
    }

    public void agregarUsuarios(Chat chat, Usuario... usuarios) {
        for (Usuario usr : usuarios) {
            agregarUsuario(usr, chat, RolEnChat.MIEMBRO);
        }
    }

    public void añadirMiembroGrupo(UUID chatId, UUID usuarioId) {
        Chat chat = chatRepository.findByIdAndTipo(chatId,TipoChat.GRUPO)
                .orElseThrow(() -> new ChatNoEncontradoException());
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException());

        agregarUsuario(usuario, chat, RolEnChat.MIEMBRO);
    }

    public boolean usuarioEstaEnGrupo(UUID chatId, UUID usuarioId) {
        if (chatId == null || usuarioId == null) {
            return false;
        }
        return usuarioChatRepository.existsById_IdUsuarioAndId_IdChat(usuarioId, chatId);
    }

    public boolean esAdmin(UUID idUsuario, UUID idChat) {

        UsuarioChat uc = usuarioChatRepository
            .findChatsByUsuarioIdAndId_IdChat(idUsuario, idChat)
            .orElse(null);

        return (uc != null) && (uc.getRol() == RolEnChat.ADMIN);
    }

    public List<Chat> listarChatsPorUsuario(UUID usuarioId) {
        return usuarioChatRepository.findChatsByUsuarioId(usuarioId)
                .stream()
                .map(usrChat -> usrChat.getChat())
                .toList();
    }

    public List<UsuarioChat> obtenerMiembrosDeGrupo(UUID chatId) {
        return usuarioChatRepository.findById_IdChat(chatId);
    }

    public void quitarMiembroGrupo(UUID chatId, UUID usuarioId) {
        Chat chat = chatRepository.findById(chatId).orElse(null);
        if (chat == null) {
            throw new ChatNoEncontradoException();
        }

        usuarioChatRepository.deleteById(new I_UsuarioChatId(usuarioId, chatId));

        List<UsuarioChat> usrsChat = usuarioChatRepository.findById_IdChat(chatId);

        if (usrsChat.isEmpty()) {
            chatRepository.delete(chat);
        }
        else if (chat.getOwner().getId().equals(usuarioId)) {
            List<UsuarioChat> adminsChat = usrsChat.stream()
                    .filter(uc -> uc.getRol().equals(RolEnChat.ADMIN))
                    .toList();

            UsuarioChat nuevoOwner;

            if (!adminsChat.isEmpty()) {
                nuevoOwner = adminsChat.get(new Random().nextInt(adminsChat.size()));
            }
            else {
                nuevoOwner = usrsChat.get(new Random().nextInt(usrsChat.size()));

                nuevoOwner.setRol(RolEnChat.ADMIN);

                usuarioChatRepository.save(nuevoOwner);
            }

            chat.setOwner(nuevoOwner.getUsuario());
            chatRepository.save(chat);
        }
    }
}
