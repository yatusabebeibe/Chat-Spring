package com.jesus.proyecto.chat.mensajes.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.jesus.proyecto.chat.archivoMensaje.entity.ArchivoMensaje;
import com.jesus.proyecto.chat.mensajes.dto.CrearMensajeRequest;
import com.jesus.proyecto.chat.mensajes.dto.MensajeResponse;
import com.jesus.proyecto.chat.mensajes.entity.Mensaje;

@Mapper(componentModel = "spring")
public interface MensajeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaEnvio", ignore = true)
    @Mapping(target = "fechaEdicion", ignore = true)
    @Mapping(target = "eliminado", ignore = true)
    @Mapping(target = "archivos", ignore = true)

    @Mapping(target = "usuario.id", ignore = true)
    @Mapping(target = "chat.id", source = "chatId")
    @Mapping(target = "mensajeRespuesta", ignore = true)
    Mensaje toEntity(CrearMensajeRequest request);

    @Mapping(target = "chatId", source = "chat.id")
    @Mapping(target = "usuarioId", source = "usuario.id")
    @Mapping(target = "mensajeRespuestaId", source = "mensajeRespuesta.id")
    @Mapping(target = "urls", source = "archivos")
    MensajeResponse toResponse(Mensaje mensaje);
    
    @Mapping(target = "chatId", source = "chat.id")
    @Mapping(target = "usuarioId", source = "usuario.id")
    @Mapping(target = "mensajeRespuestaId", source = "mensajeRespuesta.id")
    @Mapping(target = "urls", ignore = true)
    MensajeResponse toResponseSinArchivos(Mensaje mensaje);

    default List<String> map(List<ArchivoMensaje> archivos) {
        if (archivos == null) return null;

        return archivos.stream()
            .map(ArchivoMensaje::getUrl)
            .toList();
    }
}