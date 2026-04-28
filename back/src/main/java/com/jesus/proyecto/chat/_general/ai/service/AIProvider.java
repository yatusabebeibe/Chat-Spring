package com.jesus.proyecto.chat._general.ai.service;

import java.nio.file.Path;
import java.util.List;
import com.jesus.proyecto.chat._general.ai.dto.AIMessage;

public interface AIProvider {
    String chat(List<AIMessage> messages, Double temperature, Integer maxTokens);

    String transcribirAudio(Path audioPath);
}