package com.jesus.proyecto.chat.chats.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.jesus.proyecto.chat.chats.dto.ChatListResponse;
import com.jesus.proyecto.chat.chats.dto.ChatResponse;
import com.jesus.proyecto.chat.chats.entity.Chat;

@Mapper(componentModel = "spring")
public interface ChatMapper {

    @Mapping(source = "owner.id", target = "ownerId")
    ChatResponse toResponse(Chat chat);
    
    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(target = "ultimoMensaje", ignore = true)
    @Mapping(target = "avatarConversacion", ignore = true)
    ChatListResponse toListResponse(Chat chat);

}
