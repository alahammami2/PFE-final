package com.volleyball.chatbotgateway.controller;
import com.volleyball.chatbotgateway.service.GroqService;
import com.volleyball.chatbotgateway.service.PerformanceServiceClient;
import com.volleyball.chatbotgateway.service.PdfParseService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.util.Base64;

record ChatRequest(String question) {}
record AskWithFileRequest(String question, String fileBase64) {}

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    private final GroqService groqService;
    private final PerformanceServiceClient performanceClient;
    private final PdfParseService pdfParseService;

    public ChatbotController(GroqService groqService,
                             PerformanceServiceClient performanceClient,
                             PdfParseService pdfParseService) {
        this.groqService = groqService;
        this.performanceClient = performanceClient;
        this.pdfParseService = pdfParseService;
    }

    @PostMapping("/ask")
    public Mono<String> askQuestion(@RequestBody ChatRequest request) {
        return groqService.getSimpleResponse(request.question());
    }

    // Ask Gemini using a stored file by ID: focuses on the CLUB OLYMPIQUE (COK) table block by default
    @PostMapping("/files/{fileId}/ask")
    public Mono<String> askWithStoredFile(@PathVariable("fileId") Long fileId,
                                          @RequestParam(value = "team", required = false) String team,
                                          @RequestBody ChatRequest request) {
        return performanceClient.downloadFile(fileId)
                .flatMap(bytes -> pdfParseService.extractCokBlockText(bytes, team))
                .flatMap(text -> groqService.askWithContext(request.question(), text));
    }

    // Ask Gemini using ASCII table as context (more structured for Q&A)
    @PostMapping("/files/{fileId}/ask-table")
    public Mono<String> askWithStoredFileAscii(@PathVariable("fileId") Long fileId,
                                               @RequestParam(value = "team", required = false) String team,
                                               @RequestBody ChatRequest request) {
        return performanceClient.downloadFile(fileId)
                .flatMap(bytes -> pdfParseService.extractCokAsciiTable(bytes, team))
                .flatMap(table -> groqService.askWithContext(request.question(), table));
    }

    // Ask Gemini by sending base64-encoded file in JSON body (avoids multipart); focuses on COK block
    @PostMapping("/ask-with-file")
    public Mono<String> askWithFile(@RequestParam(value = "team", required = false) String team,
                                    @RequestBody AskWithFileRequest request) {
        return Mono.fromCallable(() -> Base64.getDecoder().decode(request.fileBase64()))
                .flatMap(bytes -> pdfParseService.extractCokBlockText(bytes, team))
                .flatMap(text -> groqService.askWithContext(request.question(), text));
    }

    // Ask Gemini (base64 file) using ASCII table context
    @PostMapping("/ask-with-file-table")
    public Mono<String> askWithFileAscii(@RequestParam(value = "team", required = false) String team,
                                         @RequestBody AskWithFileRequest request) {
        return Mono.fromCallable(() -> Base64.getDecoder().decode(request.fileBase64()))
                .flatMap(bytes -> pdfParseService.extractCokAsciiTable(bytes, team))
                .flatMap(table -> groqService.askWithContext(request.question(), table));
    }

    // --- New endpoints ---

    @GetMapping("/files")
    public Mono<String> listStoredFiles() {
        return performanceClient.listFiles();
    }

    @GetMapping("/files/{fileId}/players")
    public Mono<String[]> getPlayersFromFile(@PathVariable("fileId") Long fileId,
                                             @RequestParam(value = "team", required = false) String team) {
        return performanceClient.downloadFile(fileId)
                .flatMap(bytes -> pdfParseService.extractPlayers(bytes, team));
    }

    // Return raw extracted text (PDF/DOCX/TXT) for inspection/testing
    @GetMapping(value = "/files/{fileId}/text", produces = MediaType.TEXT_PLAIN_VALUE)
    public Mono<String> getFileText(@PathVariable("fileId") Long fileId) {
        return performanceClient.downloadFile(fileId)
                .flatMap(bytes -> pdfParseService.extractRawText(bytes));
    }

    // Return only the CLUB OLYMPIQUE (COK) table block, cleaned as text
    @GetMapping(value = "/files/{fileId}/text-cok", produces = MediaType.TEXT_PLAIN_VALUE)
    public Mono<String> getFileTextCok(@PathVariable("fileId") Long fileId,
                                       @RequestParam(value = "team", required = false) String team) {
        return performanceClient.downloadFile(fileId)
                .flatMap(bytes -> pdfParseService.extractCokBlockText(bytes, team));
    }

    // Return the CLUB OLYMPIQUE (COK) table as a fixed-width ASCII table
    @GetMapping(value = "/files/{fileId}/text-cok-table", produces = MediaType.TEXT_PLAIN_VALUE)
    public Mono<String> getFileTextCokTable(@PathVariable("fileId") Long fileId,
                                            @RequestParam(value = "team", required = false) String team) {
        return performanceClient.downloadFile(fileId)
                .flatMap(bytes -> pdfParseService.extractCokAsciiTable(bytes, team));
    }

    // Debug JSON: lines of detected team block and names
    @GetMapping(value = "/files/{fileId}/debug/team-block", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> debugTeamBlock(@PathVariable("fileId") Long fileId,
                                       @RequestParam(value = "team", required = false) String team) {
        return performanceClient.downloadFile(fileId)
                .flatMap(bytes -> pdfParseService.debugTeamBlock(bytes, team));
    }

    // Debug JSON: token matrix per player row for COK block
    @GetMapping(value = "/files/{fileId}/debug/cok-matrix", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> getCokTokenMatrix(@PathVariable("fileId") Long fileId,
                                          @RequestParam(value = "team", required = false) String team) {
        return performanceClient.downloadFile(fileId)
                .flatMap(bytes -> pdfParseService.extractCokTokenMatrix(bytes, team));
    }

    // LLM-based players extraction (sends document text to Gemini)
    @GetMapping("/files/{fileId}/players-llm")
    public Mono<String> getPlayersFromFileLLM(@PathVariable("fileId") Long fileId) {
        return performanceClient.downloadFile(fileId)
                .flatMap(bytes -> pdfParseService.extractRawText(bytes))
                .flatMap(text -> groqService.extractPlayersWithLLM(text));
    }

    @GetMapping("/files/{fileId}/players/{player}/stats")
    public Mono<String> getPlayerStatsFromFile(@PathVariable("fileId") Long fileId,
                                               @PathVariable("player") String player,
                                               @RequestParam(value = "team", required = false) String team) {
        return performanceClient.downloadFile(fileId)
                .flatMap(bytes -> pdfParseService.extractPlayerStats(bytes, team, player));
    }

    // LLM-based stats extraction for a given player (COK table only)
    @GetMapping("/files/{fileId}/players/{player}/stats-llm")
    public Mono<String> getPlayerStatsFromFileLLM(@PathVariable("fileId") Long fileId,
                                                  @PathVariable("player") String player) {
        return performanceClient.downloadFile(fileId)
                .flatMap(bytes -> pdfParseService.extractRawText(bytes))
                .flatMap(text -> groqService.extractStatsWithLLM(text, player));
    }

    // Debug endpoint: returns the raw lines detected for the team block and the names parsed from it
    @GetMapping("/files/{fileId}/team-debug")
    public Mono<String> debugTeam(@PathVariable("fileId") Long fileId,
                                  @RequestParam(value = "team", required = false) String team) {
        return performanceClient.downloadFile(fileId)
                .flatMap(bytes -> pdfParseService.debugTeamBlock(bytes, team));
    }
}