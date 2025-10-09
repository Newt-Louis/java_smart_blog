package ngoc.connect_gemini_api.controller;

import ngoc.connect_gemini_api.adapter.GeminiApiClient;
import ngoc.connect_gemini_api.dto.request.geminiapi.GeminiPromptRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class GeminiController {
    private final GeminiApiClient geminiApiClient;
    public GeminiController(GeminiApiClient geminiApiClient) {
        this.geminiApiClient = geminiApiClient;
    }
    @PostMapping("/gemini")
    public ResponseEntity<?> generateContent(@RequestBody GeminiPromptRequest request) {
        // Tạm thời, chúng ta chỉ giả lập việc gọi API
        // Bằng cách lặp lại prompt của người dùng trong một thẻ H1 và P
        String prompt = request.getPrompt();

        // Giả lập độ trễ mạng
//        try {
//            Thread.sleep(1500); // 1.5 giây
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }
        GeminiApiClient.GeminiApiResponse geminiResponse = geminiApiClient.generateContent(prompt);

        String fakeGeneratedContent = "<h1>Đây là nội dung cho prompt:</h1><p><em>" + prompt + "</em></p><p>Nội dung chi tiết hơn sẽ được Gemini API thật tạo ra ở bước tiếp theo.</p>";

        return ResponseEntity.ok(Map.of("generatedContent", geminiResponse));
    }
}
