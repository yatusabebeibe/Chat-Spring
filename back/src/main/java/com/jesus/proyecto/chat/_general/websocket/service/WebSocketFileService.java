package com.jesus.proyecto.chat._general.websocket.service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.jesus.proyecto.chat._general.websocket.utils.WebSocketUtils;
import com.jesus.proyecto.chat.archivoMensaje.dto.ArchivoRequest;
import com.jesus.proyecto.chat.archivoMensaje.entity.ArchivoMensaje;
import com.jesus.proyecto.chat.archivoMensaje.entity.I_ArchivoMensajeId;
import com.jesus.proyecto.chat.archivoMensaje.repository.ArchivoMensajeRepository;
import com.jesus.proyecto.chat.mensajes.dto.SessionMessageState;
import com.jesus.proyecto.chat.mensajes.entity.Mensaje;
import com.jesus.proyecto.chat.mensajes.mapper.MensajeMapper;
import com.jesus.proyecto.chat.mensajes.repository.MensajeRepository;
import com.jesus.proyecto.chat.usuarios.entity.Usuario;
import com.jesus.proyecto.chat.usuarios.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class WebSocketFileService {

    private final WebSocketUtils webSocketUtils;
    private final WebSocketSessionService sessionService;
    private final WebSocketMessageService messageService;
    private final ObjectMapper objectMapper;
    private final ArchivoMensajeRepository archivoMensajeRepository;
    private final MensajeMapper mensajeMapper;
    private final MensajeRepository mensajeRepository;
    private final UsuarioRepository usuarioRepository;

    @Value("${file.max-size-mb}")
    private int maxFileSizeMb;

    private int getMaxFileSize() {
        return this.maxFileSizeMb * 1024 * 1024;
    }

    // -------------------------
    // BINARY
    // -------------------------
    public void handleBinary(WebSocketSession session, BinaryMessage message) {
        SessionMessageState state = sessionService.getState(session);

        if (state == null) {
            mandarError(session, "No hay mensaje pendiente para enviar");
            return;
        }

        ByteBuffer buffer = message.getPayload();

        if (buffer.remaining() > getMaxFileSize()) {
            mandarError(
                session,
                "El archivo supera los " + maxFileSizeMb + "MB bufer=" + buffer.remaining() + " maximo=" + getMaxFileSize()
            );
            return;
        }

        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);

        int index = state.getReceivedFiles();

        state.getArchivos().put(index, data);
        state.setReceivedFiles(index + 1);

        if (state.getReceivedFiles() == state.getExpectedFiles()) {
            try {
                guardarArchivos(state);
            } catch (IllegalArgumentException e) {
                mandarError(session, "Error de datos: " + e.getMessage());
                return;
            } catch (Exception e) {
                mandarError(session, "Error inesperado al procesar mensaje: " + e.getMessage());
                return;
            }

            try {
                messageService.finalizarMensaje(session, state);
            } catch (Exception e) {
                mandarError(session, "Error al finalizar mensaje: " + e.getMessage());
            }
        }
    }

    // -------------------------
    // GUARDAR ARCHIVOS
    // -------------------------
    @Transactional
    private void guardarArchivos(SessionMessageState state) {
        Usuario usuario;
        if (state.getUsuarioId() == null) {
            throw new RuntimeException("getUsuarioId es null " + state.toString());
        }
        try {
            usuario = usuarioRepository.findById(state.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no existe"));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Error al obtener usuario: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al buscar usuario", e);
        }

        Mensaje mensaje = mensajeMapper.toEntity(state.getRequest());
        mensaje.setUsuario(usuario);
        mensaje.setTipo(webSocketUtils.calcularTipoMensaje(state));

        try {
            mensajeRepository.save(mensaje);
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar mensaje: "+ e.getMessage(), e);
        }

        for (Map.Entry<Integer, byte[]> entry : state.getArchivos().entrySet()) {

            int index = entry.getKey();
            byte[] data = entry.getValue();
            ArchivoRequest meta = state.getRequest().getArchivos().get(index);

            String nombreOriginal = meta.getNombre();
            String safeFilename = generarNombreSeguro(state, index, nombreOriginal);

            Path path = Paths.get("uploads/" + state.getChatId() + "/" + safeFilename);

            try {
                Files.createDirectories(path.getParent());
            } catch (IOException e) {
                throw new RuntimeException("Error creando directorio", e);
            }

            try {
                Files.write(path, data);
            } catch (IOException e) {
                throw new RuntimeException("Error escribiendo archivo: " + safeFilename, e);
            }

            ArchivoMensaje archivo = new ArchivoMensaje();

            I_ArchivoMensajeId id = new I_ArchivoMensajeId();
            id.setMsgId(state.getMessageId());
            id.setIndice(index);

            archivo.setId(id);
            archivo.setUrl(safeFilename);
            archivo.setMensaje(mensaje);

            try {
                archivoMensajeRepository.save(archivo);
            } catch (Exception e) {
                throw new RuntimeException("Error guardando metadata de archivo", e);
            }
        }
    }

    private String generarNombreSeguro(SessionMessageState state, int index, String nombreOriginal) {

        String extension = "";

        if (nombreOriginal != null && !nombreOriginal.isEmpty()) {
            int extIndex = nombreOriginal.lastIndexOf('.');

            if (extIndex > 0 && extIndex < nombreOriginal.length() - 1) {
                extension = nombreOriginal.substring(extIndex);

                int tamañoMaximo = 6;
                if (extension.length() > tamañoMaximo) {
                    extension = extension.substring(0, tamañoMaximo);
                }
            }
        }

        if (extension.isEmpty()) {
            extension = ".bin";
        }

        String baseId = state.getMessageId().toString();
        String indexStr = String.format("%02d", index);

        return baseId + "_" + indexStr + extension;
    }

    private void mandarError(WebSocketSession session, String msg) {
        try {
            Map<String, Object> error = new HashMap<>();
            error.put("error", msg);

            // Enviar respuesta de error en texto plano JSON
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(error)));
            // Nota: Podrías llamar a session.close() aquí si deseas desconectar inmediatamente
        } catch (Exception e) {
            // Si no podemos enviar el error, imprimir a stderr para debugging
            e.printStackTrace();
        }
    }
}