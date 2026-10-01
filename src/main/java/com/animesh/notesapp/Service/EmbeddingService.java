package com.animesh.notesapp.Service;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.pgvector.PGvector;
import org.springframework.http.MediaType;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmbeddingService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public EmbeddingService() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(2000);   // ms, fail fast if Ollama is down
        factory.setReadTimeout(30000);     // ms, first call can be slow while the model loads

        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:11434")
                .requestFactory(factory)
                .build();
    }

    public PGvector generateEmbedding(String text) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("model", "nomic-embed-text");
        requestBody.put("prompt", text);

        String response = restClient.post()
                .uri("/api/embeddings")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(String.class);

        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode embeddingArray = root.get("embedding");

            float[] vector = new float[embeddingArray.size()];
            for (int i = 0; i < embeddingArray.size(); i++) {
                vector[i] = (float) embeddingArray.get(i).asDouble();
            }

            return new PGvector(vector);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate embedding", e);
        }
    }
}