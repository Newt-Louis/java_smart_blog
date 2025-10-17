package ngoc.connect_gemini_api.controller;

import ngoc.connect_gemini_api.dto.request.geminiapi.GeminiPromptRequest;
import ngoc.connect_gemini_api.service.GeminiApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class GeminiController {
    private final GeminiApiService geminiApiService;
    public GeminiController(GeminiApiService geminiApiService) {
        this.geminiApiService = geminiApiService;
    }
    @PostMapping("/gemini")
    public ResponseEntity<?> generateContent(@RequestBody GeminiPromptRequest request) {

        String prompt = request.getPrompt();

        String geminiResponse = geminiApiService.generateBlogPost(prompt);

        return ResponseEntity.ok(Map.of("generatedContent", geminiResponse));
    }
}
