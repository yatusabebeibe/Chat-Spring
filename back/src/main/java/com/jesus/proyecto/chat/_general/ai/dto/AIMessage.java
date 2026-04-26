package com.jesus.proyecto.chat._general.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AIMessage {
    private String role;   // system | user | assistant
    private String content;
}