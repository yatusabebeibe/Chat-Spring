    package com.jesus.proyecto.chat._general.ai.service;

    import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jesus.proyecto.chat._general.ai.dto.AIMessage;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class OpenAiApi implements AIProvider {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${openai.api.key}")
    private String API_KEY;

    @Value("${openai.api.url}")
    private String URL;

    @Value("${openai.model}")
    private String MODEL;


    @Override
    public String chat(List<AIMessage> messages, Double temperature, Integer maxTokens) {
        Map<String, Object> body = new HashMap<>();
        body.put("model", MODEL);
        body.put("messages", messages);
        if (temperature != null) {
            body.put("temperature", temperature);
        }
        if (maxTokens != null) {
            body.put("max_tokens", maxTokens);
        }

        try {
            String json = objectMapper.writeValueAsString(body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .timeout(java.time.Duration.ofSeconds(10))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + API_KEY)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpClient client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .connectTimeout(java.time.Duration.ofSeconds(10))
                    .build();
            
            HttpResponse<String> response;
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return null;
            }
            
            System.out.println();
            System.out.println("-- Body --");
            System.out.println(response.body());
            System.out.println("----");
            System.out.println();

            // respuesta simplificada (puedes mejorarlo luego con DTO real)
            return objectMapper
                    .readTree(response.body())
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asString();

        } catch (Exception e) {
            throw new RuntimeException("Error IA: " + e.getMessage());
        }
    }
}