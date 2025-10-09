package ngoc.connect_gemini_api.adapter;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class GeminiApiClient {
    private final WebClient webClient;
    private final String apiKey;
    private final String apiUrl;

    public GeminiApiClient(WebClient.Builder webClientBuilder, @Value("${gemini.api.key")String apiKey, @Value("${gemini.api.url}")String apiUrl) {
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
        this.webClient = webClientBuilder.baseUrl(apiUrl).build();
    }

    /**
     * Gọi đến Gemini API để tạo nội dung.
     * @param prompt Prompt cuối cùng đã qua xử lý.
     * @return Đối tượng Response từ Gemini.
     */
    public GeminiApiResponse generateContent(String prompt) {
        // 1. Tạo đối tượng Request Body theo đúng định dạng của Gemini API
//        GeminiRequest.Part part = new GeminiRequest.Part(prompt);
//        GeminiRequest.Content content = new GeminiRequest.Content(List.of(part));
//        GeminiRequest requestBody = new GeminiRequest(List.of(content));
        Part part = new Part(prompt);
        Content content = new Content(List.of(part));
        GeminiRequest requestBody = new GeminiRequest(List.of(content));

        // 2. Thực hiện gọi API bằng WebClient
        try {
            return webClient.post()
                    .uri(uriBuilder -> uriBuilder.queryParam("key", this.apiKey).build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve() // Gửi request và nhận response
                    .bodyToMono(GeminiApiResponse.class) // Chuyển response body thành object GeminiApiResponse
                    .block(); // Chờ và nhận kết quả (sử dụng block cho luồng đồng bộ đơn giản)
        } catch (WebClientResponseException e) {
            System.err.println("==================== GEMINI API ERROR ====================");
            System.err.println("Error Status Code: " + e.getStatusCode());
            System.err.println("Error Response Body: " + e.getResponseBodyAsString());
            System.err.println("==========================================================");

            // Ném lại lỗi để các lớp bên trên vẫn biết rằng có sự cố xảy ra
            throw e;
        }
    }

    // --- CÁC LỚP DTO ĐỂ ÁNH XẠ JSON REQUEST VÀ RESPONSE ---
    // Bạn có thể tạo file riêng cho chúng trong package dto/geminiapi

    // DTO cho Request Body
    public record GeminiRequest(List<Content> contents) {}
    public record Content(List<Part> parts) {}
    public record Part(String text) {}

    // DTO cho Response Body (chỉ lấy những trường cần thiết)
    public record GeminiApiResponse(List<Candidate> candidates) {
        // Hàm tiện ích để lấy text từ candidate đầu tiên
        public String getGeneratedText() {
            if (candidates != null && !candidates.isEmpty()) {
                Candidate firstCandidate = candidates.getFirst();
                if (firstCandidate.content() != null && !firstCandidate.content().parts().isEmpty()) {
                    return firstCandidate.content().parts().getFirst().text();
                }
            }
            return "No content generated.";
        }
    }
    public record Candidate(Content content) {}
}
