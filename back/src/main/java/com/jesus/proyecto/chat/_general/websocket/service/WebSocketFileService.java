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

import com.jesus.proyecto.chat.archivoMensaje.dto.ArchivoRequest;
import com.jesus.proyecto.chat.archivoMensaje.utils.PathValidations;
import com.jesus.proyecto.chat.mensajes.dto.SessionMessageState;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class WebSocketFileService {

    private final WebSocketSessionService sessionService;
    private final WebSocketMessageService messageService;
    private final ObjectMapper objectMapper;

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
            guardarArchivos(state);

            messageService.finalizarMensaje(session, state);
        }
    }

    // -------------------------
    // GUARDAR ARCHIVOS
    // -------------------------
    @Transactional
    private void guardarArchivos(SessionMessageState state) {
        for (Map.Entry<Integer, byte[]> entry : state.getArchivos().entrySet()) {

            int index = entry.getKey();
            byte[] data = entry.getValue();
            ArchivoRequest meta = state.getRequest().getArchivos().get(index);

            String nombreOriginal = meta.getNombre();
            String safeFilename = generarNombreSeguro(state, index, nombreOriginal);

            Path path = Paths.get(
                PathValidations.BASE_UPLOAD,
                PathValidations.CARPETA_CHATS,
                state.getChatId().toString(),
                safeFilename
            );

            try {
                Files.createDirectories(path.getParent());
            } catch (IOException e) {
                throw new RuntimeException("Error creando directorio", e);
            }

            try {
                Files.write(path, data);
                state.getRutasArchivos().put(index, safeFilename);
            } catch (IOException e) {
                throw new RuntimeException("Error escribiendo archivo: " + safeFilename, e);
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