package com.jesus.proyecto.chat._general.ai.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.jesus.proyecto.chat._general.ai.dto.AIMessage;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class OpenAiApi implements AIProvider {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${openai.api.key}")
    private String API_KEY;

    @Value("${openai.api.audio.key}")
    private String API_KEY_AUDIO;

    @Value("${openai.api.url}")
    private String URL;

    @Value("${openai.api.audio.url}")
    private String URL_AUDIO;

    @Value("${openai.model}")
    private String MODEL;

    @Value("${openai.audio.model}")
    private String MODEL_AUDIO;

    // =========================
    // CHAT (HttpClient)
    // =========================
    @Override
    public String chat(List<AIMessage> messages, Double temperature, Integer maxTokens) {
        try {
            String json = buildChatBody(messages, temperature, maxTokens);
            HttpRequest request = buildJsonRequest(json, URL);
            HttpResponse<String> response = sendRequest(request);

            logResponse(response.body());

            var root = objectMapper.readTree(response.body());

            if (root.has("error")) {
                throw new RuntimeException("IA error: " + root.path("error").path("message").asString());
            }

            return parseChatResponse(response.body());

        } catch (Exception e) {
            throw new RuntimeException("Error IA: " + e.getMessage());
        }
    }

    private String buildChatBody(List<AIMessage> messages, Double temperature, Integer maxTokens) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("model", MODEL);
        body.put("messages", messages);

        if (temperature != null) body.put("temperature", temperature);
        if (maxTokens != null) body.put("max_tokens", maxTokens);

        return objectMapper.writeValueAsString(body);
    }

    private HttpRequest buildJsonRequest(String json, String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
    }

    private String parseChatResponse(String body) throws Exception {
        return objectMapper
                .readTree(body)
                .path("choices")
                .get(0)
                .path("message")
                .path("content")
                .asString();
    }

    // =========================
    // AUDIO (REST TEMPLATE - SIN BOUNDARY)
    // =========================
    @Override
    public String transcribirAudio(Path audioPath) {
        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("model", MODEL_AUDIO);
            body.add("file", new FileSystemResource(audioPath));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.setBearerAuth(API_KEY_AUDIO);

            HttpEntity<MultiValueMap<String, Object>> request =
                    new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    URL_AUDIO,
                    request,
                    String.class
            );

            logResponse(response.getBody());

            return parseAudioResponse(response.getBody());

        } catch (Exception e) {
            throw new RuntimeException("Error transcribiendo audio: " + e.getMessage());
        }
    }

    private String parseAudioResponse(String body) throws Exception {
        return objectMapper
                .readTree(body)
                .path("text")
                .asString();
    }

    // =========================
    // COMMON
    // =========================
    private HttpResponse<String> sendRequest(HttpRequest request) throws Exception {
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private void logResponse(String body) {
        System.out.println();
        System.out.println("-- Body --");
        System.out.println(body);
        System.out.println("----");
        System.out.println();
    }
}