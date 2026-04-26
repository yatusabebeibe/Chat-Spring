package com.jesus.proyecto.chat._general.ai.service;

import java.util.List;
import com.jesus.proyecto.chat._general.ai.dto.AIMessage;

public interface AIProvider {
    String chat(List<AIMessage> messages, Double temperature, Integer maxTokens);
}