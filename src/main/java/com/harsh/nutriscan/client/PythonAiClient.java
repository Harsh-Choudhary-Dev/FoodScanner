package com.harsh.nutriscan.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Component
public class PythonAiClient {
    private final WebClient webClient;

    public PythonAiClient(WebClient.Builder builder, @Value("${food.scanner.ai.base-url}") String url) {
        this.webClient = builder
                .baseUrl(url) // your Python API
                .build();
    }

    public Map identifyProduct(Path imagePaths) {
        return webClient.post()
                .uri("/identify")
                .bodyValue(Map.of("images_path",imagePaths))
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }
}
