package com.jesus.proyecto.chat.relacionUsuarioChat.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.jesus.proyecto.chat.relacionUsuarioChat.entity.UsuarioChat;
import com.jesus.proyecto.chat.usuarios.dto.UsuarioChatResponse;

@Mapper(componentModel = "spring")
public interface UsuarioChatMapper {

    @Mapping(target = "id", source = "usuario.id")
    @Mapping(target = "usuario", source = "usuario.usuario")
    @Mapping(target = "nombre", source = "usuario.nombre")
    @Mapping(target = "rol", source = "rol")
    @Mapping(target = "extensionAvatar", ignore = true)
    UsuarioChatResponse toResponse(UsuarioChat usuarioChat);

}
