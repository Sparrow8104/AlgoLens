package com.algolens.algo_lens.client;

import com.algolens.algo_lens.config.GeminiProperties;
import com.algolens.algo_lens.exception.AiServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiClient {

    private final GeminiProperties geminiProperties;
    private final ObjectMapper objectMapper;

    private final WebClient geminiWebClient=WebClient.builder()
            .baseUrl("https://generativelanguage.googleapis.com")
            .build();

    public String generate(String prompt){
        try{
            Map<String,Object> requestBody=Map.of(
                "contents",new Object[]{
                        Map.of("parts",new Object[]{
                                Map.of("text",prompt)
                        })
                },
                    "generationConfig",Map.of(
                            "temperature",0.3,
                            "maxOutputTokens",1500
                    )
            );

            String response=geminiWebClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1beta/models/gemini-1.5-flash:generateContent")
                            .queryParam("key", geminiProperties.getKey())
                            .build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            return extractText(response);
        }catch (WebClientResponseException e){
          log.error("Gemini API error: {} {}",e.getStatusCode(),e.getResponseBodyAsString());
          throw new AiServiceException("AI Service Error: "+e.getStatusCode());
        }catch (Exception e){
            log.error("Gemini API error: {}",e.getMessage());
            throw new AiServiceException("AI service unavailable: "+e.getMessage());
        }
    }

    private String extractText(String rawResponse){
        try {
            JsonNode root= objectMapper.readTree(rawResponse);
            return root
                    .path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();
        } catch (Exception e) {
            throw new AiServiceException(
                    "Failed to parse Gemini response: "+e.getMessage()
            );
        }
    }

}
