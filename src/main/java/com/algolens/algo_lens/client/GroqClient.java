package com.algolens.algo_lens.client;

import com.algolens.algo_lens.config.GroqProperties;
import com.algolens.algo_lens.exception.AiServiceException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GroqClient {

    private final GroqProperties groqProperties;
    private final WebClient.Builder webClientBuilder;

    public String generate(String prompt) {
        try {
            WebClient groqClient = webClientBuilder
                    .baseUrl(groqProperties.getUrl())
                    .build();

            Map<String, Object> requestBody = Map.of(
                    "model", "llama-3.3-70b-versatile",
                    "messages", List.of(
                            Map.of("role", "user", "content", prompt)
                    )
            );

            JsonNode response = groqClient.post()
                    .uri("/openai/v1/chat/completions")
                    .header("Authorization", "Bearer " + groqProperties.getKey())
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            return response
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

        } catch (WebClientResponseException e) {
            log.error("Groq API error: {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new AiServiceException("AI Service Error: " + e.getStatusCode());
        } catch (Exception e) {
            log.error("Groq API error: {}", e.getMessage());
            throw new AiServiceException("AI service unavailable: " + e.getMessage());
        }
    }
}