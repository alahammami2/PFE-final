package com.volleyball.chatbotgateway.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@ConditionalOnProperty(value = "api.gemini.url")
public class GeminiService {
    private final WebClient webClient;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${api.gemini.key}")
    private String apiKey;

    public GeminiService(WebClient.Builder webClientBuilder, @Value("${api.gemini.url}") String apiUrl) {
        this.webClient = webClientBuilder.baseUrl(apiUrl).build();
    }

    // Simple Q&A
    public Mono<String> getSimpleResponse(String question) {
        String requestBody = String.format("""
            {
              "contents": [{
                "parts":[{
                  "text": "%s"
                }]
              }]
            }
            """, question);

        return this.webClient.post()
                .uri(uriBuilder -> uriBuilder.queryParam("key", this.apiKey).build())
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .exchangeToMono(resp -> resp.bodyToMono(String.class)
                        .defaultIfEmpty("")
                        .map(body -> {
                            int code = resp.statusCode().value();
                            if (code >= 200 && code < 300) return extractText(body);
                            return String.format("{\"status\":%d,\"error\":true,\"body\":%s}", code, body.isEmpty()?"\"\"":body);
                        })
                );
    }

    // Extract COK players using LLM; expect strict JSON {"players":[...]} only COK
    public Mono<String> extractPlayersWithLLM(String documentText) {
        String prompt = """
You are a strict extractor. From the following volleyball match report text, extract ONLY the players of CLUB OLYMPIQUE (COK) team. Ignore the opponent. Return JSON only, no prose, with this exact schema:\n{\n  \"players\": [\"NAME\", ...]\n}\nNames must be uppercase as in the document.\nDocument:\n""" + documentText.replace("\\", "\\\\").replace("\"", "\\\"");

        String requestBody = String.format("""
            {
              "contents": [{
                "parts":[{
                  "text": "%s"
                }]
              }]
            }
            """, prompt);

        return this.webClient.post()
                .uri(uriBuilder -> uriBuilder.queryParam("key", this.apiKey).build())
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class);
    }

    // Extract stats for a given COK player; expect strict JSON per our totals schema
    public Mono<String> extractStatsWithLLM(String documentText, String player) {
        String prompt = """
You are a strict extractor. From the following volleyball match report text, extract TOTALS for PLAYER in the CLUB OLYMPIQUE (COK) table only. Return JSON only, no prose, with this exact schema:\n{\n  \"team\": \"CLUB OLYMPIQUE KELIBIA\",\n  \"player\": \"NAME\",\n  \"total\": {\n    \"points\": 0,\n    \"serve\": {\n      \"aces\": 0, \"err\": 0, \"tot\": 0\n    },\n    \"reception\": {\n      \"tot\": 0, \"exc%\": 0, \"pos%\": 0, \"err\": 0\n    },\n    \"attack\": {\n      \"tot\": 0, \"kills\": 0, \"err\": 0, \"blk\": 0, \"eff%\": 0\n    },\n    \"block\": {\n      \"stuffs\": 0\n    }\n  }\n}\nIf a value is missing, keep 0. Use integers.\nPLAYER: """ + player + "\nDocument:\n" + documentText.replace("\\", "\\\\").replace("\"", "\\\"");

        String requestBody = String.format("""
            {
              "contents": [{
                "parts":[{
                  "text": "%s"
                }]
              }]
            }
            """, prompt);

        return this.webClient.post()
                .uri(uriBuilder -> uriBuilder.queryParam("key", this.apiKey).build())
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class);
    }

    // Generic: ask a question with document context
    public Mono<String> askWithContext(String question, String documentText) {
        String escapedDoc = documentText == null ? "" : documentText.replace("\\", "\\\\").replace("\"", "\\\"");
        String escapedQ = question == null ? "" : question.replace("\\", "\\\\").replace("\"", "\\\"");
        String prompt = """
You are a precise assistant. Answer the QUESTION using only the information from DOCUMENT. If the answer is not present, reply exactly: Not found in document.
QUESTION:
""" + escapedQ + "\nDOCUMENT:\n" + escapedDoc;

        String requestBody = String.format("""
            {
              "contents": [{
                "parts":[{
                  "text": "%s"
                }]
              }]
            }
            """, prompt);

        return this.webClient.post()
                .uri(uriBuilder -> uriBuilder.queryParam("key", this.apiKey).build())
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .exchangeToMono(resp -> resp.bodyToMono(String.class)
                        .defaultIfEmpty("")
                        .map(body -> {
                            int code = resp.statusCode().value();
                            if (code >= 200 && code < 300) return extractText(body);
                            return String.format("{\"status\":%d,\"error\":true,\"body\":%s}", code, body.isEmpty()?"\"\"":body);
                        })
                );
    }

    private String extractText(String body) {
        try {
            JsonNode root = mapper.readTree(body);
            JsonNode candidates = root.path("candidates");
            if (candidates.isArray()) {
                for (JsonNode cand : candidates) {
                    JsonNode parts = cand.path("content").path("parts");
                    if (parts.isArray()) {
                        StringBuilder sb = new StringBuilder();
                        for (JsonNode p : parts) {
                            JsonNode t = p.get("text");
                            if (t != null && !t.isNull()) sb.append(t.asText());
                        }
                        if (sb.length() > 0) return sb.toString();
                    }
                }
            }
        } catch (Exception ignored) {}
        // Fallback: return raw body if structure unexpected
        return body;
    }
}
