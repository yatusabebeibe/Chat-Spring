package com.jesus.proyecto.chat._general.websocket.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.jesus.proyecto.chat._general.websocket.utils.WebSocketAuthUtil;
import com.jesus.proyecto.chat._general.websocket.utils.WebSocketUtils;
import com.jesus.proyecto.chat.archivoMensaje.entity.ArchivoMensaje;
import com.jesus.proyecto.chat.archivoMensaje.entity.I_ArchivoMensajeId;
import com.jesus.proyecto.chat.archivoMensaje.repository.ArchivoMensajeRepository;
import com.jesus.proyecto.chat.mensajes.dto.CrearMensajeRequest;
import com.jesus.proyecto.chat.mensajes.dto.MensajeResponse;
import com.jesus.proyecto.chat.mensajes.dto.SessionMessageState;
import com.jesus.proyecto.chat.mensajes.entity.Mensaje;
import com.jesus.proyecto.chat.mensajes.mapper.MensajeMapper;
import com.jesus.proyecto.chat.mensajes.service.MensajeService;
import com.jesus.proyecto.chat.mensajes.utils.TipoMensaje;
import com.jesus.proyecto.chat.usuarios.service.UsuarioService;

import lombok.AllArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Service
@AllArgsConstructor
public class WebSocketMessageService {

    private final ObjectMapper objectMapper;
    private final MensajeService mensajeService;
    private final MensajeMapper mensajeMapper;
    private final UsuarioService usuarioService;
    private final ArchivoMensajeRepository archivoMensajeRepository;
    private final WebSocketAuthUtil authUtil;
    private final WebSocketSessionService sessionService;
    private final WebSocketRoomService roomService;
    private final WebSocketUtils webSocketUtils;

    // -------------------------
    // TEXT MESSAGE
    // -------------------------
    public void handleText(WebSocketSession session, String payload) {
        try {
            if (payload == null || payload.isEmpty()) {
                mandarError(session, "Mensaje vacío");
                return;
            }

            CrearMensajeRequest req = objectMapper.readValue(payload, CrearMensajeRequest.class);

            UUID userId = authUtil.obtenerUsuario(session);
            if (userId == null) {
                mandarError(session, "Autenticacion requerida");
                return;
            }

            UUID messageId = UUID.randomUUID();

            SessionMessageState state = new SessionMessageState();
            state.setMessageId(messageId);
            state.setUsuarioId(userId);
            state.setChatId(req.getChatId());
            state.setRequest(req);
            state.setRequest(req);

            int fileCount = req.getArchivos() != null ? req.getArchivos().size() : 0;
            state.setExpectedFiles(fileCount);

            if (fileCount == 0) {
                finalizarMensaje(session, state);
                return;
            }

            state.setRutasArchivos(new HashMap<Integer,String>());

            sessionService.saveState(session, state);

            Map<String, Object> response = new HashMap<>();
            response.put("type", "MESSAGE_READY");
            response.put("messageId", messageId.toString());

            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));

        } catch (Exception e) {
            mandarError(session, "Error procesando inicio del mensaje: " + e.getMessage());
        }
    }

    // -------------------------
    // FINALIZAR
    // -------------------------
    public void finalizarMensaje(WebSocketSession session, SessionMessageState state) {
        CrearMensajeRequest req = state.getRequest();

        TipoMensaje tipo = webSocketUtils.calcularTipoMensaje(state);
        req.setTipo(tipo);

        UUID userId = authUtil.obtenerUsuario(session);
        if (userId == null) throw new RuntimeException("No se pudo identificar usuario final");

        Mensaje mensaje = mensajeMapper.toEntity(req);
        mensaje.setUsuario(usuarioService.buscarPorId(userId));
        mensaje.setFechaEnvio(Instant.now());

        Mensaje guardado = mensajeService.guardar(mensaje);

        if (state.getRutasArchivos() != null) {
            for (Map.Entry<Integer, String> entry : state.getRutasArchivos().entrySet()) {

                int index = entry.getKey();
                String filename = entry.getValue();

                ArchivoMensaje archivo = new ArchivoMensaje();

                I_ArchivoMensajeId id = new I_ArchivoMensajeId();
                id.setMsgId(guardado.getId());
                id.setIndice(index);

                archivo.setId(id);
                archivo.setUrl(filename);
                archivo.setMensaje(guardado);

                archivoMensajeRepository.save(archivo);
            }
        }

        sessionService.remove(session);

        MensajeResponse msgRespuesta = mensajeMapper.toResponse(guardado);
        if (state.getRutasArchivos() !=null) {
            msgRespuesta.setUrls(new ArrayList<>(state.getRutasArchivos().values()));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("type", "NEW_MESSAGE");
        response.put("message", msgRespuesta);

        String msgJson = objectMapper.writeValueAsString(response);
        roomService.broadcast(state.getChatId(), msgJson);
    }

    // -------------------------
    // AUX
    // -------------------------

    private void mandarError(WebSocketSession session, String msg) {
        try {
            Map<String, Object> error = new HashMap<>();
            error.put("error", msg);

            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(error)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}