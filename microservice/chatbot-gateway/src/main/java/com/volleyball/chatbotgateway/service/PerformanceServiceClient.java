package com.volleyball.chatbotgateway.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class PerformanceServiceClient {

    private final WebClient webClient;

    public PerformanceServiceClient(@Value("${services.performance.base-url}") String baseUrl,
                                    WebClient.Builder builder) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    public Mono<String> listFiles() {
        return webClient.get()
                .uri("/api/performance/files")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<byte[]> downloadFile(Long fileId) {
        return webClient.get()
                .uri("/api/performance/files/{id}/download", fileId)
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .retrieve()
                .bodyToMono(byte[].class);
    }
}
