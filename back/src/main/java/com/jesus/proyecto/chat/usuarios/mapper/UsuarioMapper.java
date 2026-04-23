package com.jesus.proyecto.chat.usuarios.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.jesus.proyecto.chat.usuarios.dto.RegistroRequest;
import com.jesus.proyecto.chat.usuarios.dto.UsuarioRequest;
import com.jesus.proyecto.chat.usuarios.dto.UsuarioResponse;
import com.jesus.proyecto.chat.usuarios.entity.Usuario;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    UsuarioResponse toResponse(Usuario usuario);

    @Mapping(target = "nombre", ignore = true)
    @Mapping(target = "chats", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaUltimaConexion", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    Usuario toEntity(UsuarioRequest response);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "chats", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaUltimaConexion", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    Usuario toEntity(RegistroRequest request);
}