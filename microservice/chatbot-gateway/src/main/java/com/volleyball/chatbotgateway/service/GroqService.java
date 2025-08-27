package com.volleyball.chatbotgateway.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class GroqService {
    private final WebClient webClient;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${api.groq.key}")
    private String apiKey;

    @Value("${api.groq.model:llama-3.1-8b-instant}")
    private String model;

    public GroqService(WebClient.Builder webClientBuilder, @Value("${api.groq.url}") String apiUrl) {
        this.webClient = webClientBuilder
                .baseUrl(apiUrl)
                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    // Simple Q&A
    public Mono<String> getSimpleResponse(String question) {
        String requestBody = String.format(
                """
                {"model":"%s","messages":[{"role":"user","content":"%s"}],"stream":false}
                """,
                model, escape(question)
        );

        return this.webClient.post()
                .header("Authorization", "Bearer " + (apiKey == null ? "" : apiKey))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchangeToMono(resp -> resp.bodyToMono(String.class)
                        .defaultIfEmpty("")
                        .map(body -> {
                            int code = resp.statusCode().value();
                            if (code >= 200 && code < 300) return extractContent(body);
                            return String.format("{\"status\":%d,\"error\":true,\"body\":%s}", code, body.isEmpty()?"\"\"":body);
                        })
                );
    }

    // Ask with document context
    public Mono<String> askWithContext(String question, String documentText) {
        String prompt = "You are a precise assistant. Answer the QUESTION using only the information from DOCUMENT. If the answer is not present, reply exactly: Not found in document.\nQUESTION:\n"
                + (question == null ? "" : question) + "\nDOCUMENT:\n" + (documentText == null ? "" : documentText);

        String requestBody = String.format(
                """
                {"model":"%s","messages":[{"role":"user","content":"%s"}],"stream":false}
                """,
                model, escape(prompt)
        );

        return this.webClient.post()
                .header("Authorization", "Bearer " + (apiKey == null ? "" : apiKey))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchangeToMono(resp -> resp.bodyToMono(String.class)
                        .defaultIfEmpty("")
                        .map(body -> {
                            int code = resp.statusCode().value();
                            if (code >= 200 && code < 300) return extractContent(body);
                            return String.format("{\"status\":%d,\"error\":true,\"body\":%s}", code, body.isEmpty()?"\"\"":body);
                        })
                );
    }

    // Extract COK players using LLM; expect strict JSON {"players":[...]}
    public Mono<String> extractPlayersWithLLM(String documentText) {
        String prompt = "You are a strict extractor. From the following volleyball match report text, extract ONLY the players of CLUB OLYMPIQUE (COK) team. Ignore the opponent. Return JSON only, no prose, with this exact schema:\n{\n  \"players\": [\"NAME\", ...]\n}\nNames must be uppercase as in the document.\nDocument:\n" + (documentText == null ? "" : documentText);

        String requestBody = String.format(
                """
                {"model":"%s","messages":[{"role":"user","content":"%s"}],"stream":false}
                """,
                model, escape(prompt)
        );

        return this.webClient.post()
                .header("Authorization", "Bearer " + (apiKey == null ? "" : apiKey))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .map(this::extractContent);
    }

    // Extract stats for a given COK player; expect strict JSON per totals schema
    public Mono<String> extractStatsWithLLM(String documentText, String player) {
        String prompt = "You are a strict extractor. From the following volleyball match report text, extract TOTALS for PLAYER in the CLUB OLYMPIQUE (COK) table only. Return JSON only, no prose, with this exact schema:\n{\n  \"team\": \"CLUB OLYMPIQUE KELIBIA\",\n  \"player\": \"NAME\",\n  \"total\": {\n    \"points\": 0,\n    \"serve\": {\n      \"aces\": 0, \"err\": 0, \"tot\": 0\n    },\n    \"reception\": {\n      \"tot\": 0, \"exc%\": 0, \"pos%\": 0, \"err\": 0\n    },\n    \"attack\": {\n      \"tot\": 0, \"kills\": 0, \"err\": 0, \"blk\": 0, \"eff%\": 0\n    },\n      \"block\": {\n      \"stuffs\": 0\n    }\n  }\n}\nIf a value is missing, keep 0. Use integers.\nPLAYER: " + (player == null ? "" : player) + "\nDocument:\n" + (documentText == null ? "" : documentText);

        String requestBody = String.format(
                """
                {"model":"%s","messages":[{"role":"user","content":"%s"}],"stream":false}
                """,
                model, escape(prompt)
        );

        return this.webClient.post()
                .header("Authorization", "Bearer " + (apiKey == null ? "" : apiKey))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .map(this::extractContent);
    }

    private String extractContent(String body) {
        try {
            JsonNode root = mapper.readTree(body);
            JsonNode choices = root.path("choices");
            if (choices.isArray() && choices.size() > 0) {
                JsonNode msg = choices.get(0).path("message");
                JsonNode content = msg.path("content");
                if (content.isTextual()) return content.asText();
            }
        } catch (Exception ignored) {}
        return body; // fallback
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
