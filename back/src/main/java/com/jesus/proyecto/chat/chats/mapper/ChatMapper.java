package com.jesus.proyecto.chat.chats.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.jesus.proyecto.chat.chats.dto.ChatListResponse;
import com.jesus.proyecto.chat.chats.dto.ChatResponse;
import com.jesus.proyecto.chat.chats.entity.Chat;

@Mapper(componentModel = "spring")
public interface ChatMapper {

    @Mapping(source = "creador.id", target = "idCreador")
    ChatResponse toResponse(Chat chat);
    
    @Mapping(source = "creador.id", target = "idCreador")
    @Mapping(target = "ultimoMensaje", ignore = true)
    ChatListResponse toListResponse(Chat chat);

}
