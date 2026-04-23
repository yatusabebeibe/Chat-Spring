package com.jesus.proyecto.chat.mensajes.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.jesus.proyecto.chat.mensajes.dto.MensajeRequest;
import com.jesus.proyecto.chat.mensajes.dto.MensajeResponse;
import com.jesus.proyecto.chat.mensajes.entity.Mensaje;

@Mapper(componentModel = "spring")
public interface MensajeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaEnvio", ignore = true)
    @Mapping(target = "fechaEdicion", ignore = true)
    @Mapping(target = "eliminado", ignore = true)

    @Mapping(target = "chat.id", source = "chatId")
    @Mapping(target = "usuario.id", source = "usuarioId")
    @Mapping(target = "mensajeRespuesta.id", ignore = true /* source = "mensajeRespuestaId" */)
    Mensaje toEntity(MensajeRequest request);

    @Mapping(target = "chatId", source = "chat.id")
    @Mapping(target = "usuarioId", source = "usuario.id")
    @Mapping(target = "mensajeRespuestaId", source = "mensajeRespuesta.id")
    MensajeResponse toResponse(Mensaje mensaje);
}