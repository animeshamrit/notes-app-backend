package com.animesh.notesapp.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.animesh.notesapp.Service.EmbeddingService;

@RestController
public class PingController {

    private final EmbeddingService embeddingService;

    PingController(EmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
    }

    @GetMapping("/test-embedding")
    public String testEmbedding() {
    var vector = embeddingService.generateEmbedding("I need to buy a new umbrella");
    return "Generated vector with " + vector.toArray().length + " dimensions";
    }

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}
