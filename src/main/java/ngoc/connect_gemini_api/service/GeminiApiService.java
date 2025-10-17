package ngoc.connect_gemini_api.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import ngoc.connect_gemini_api.adapter.GeminiApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GeminiApiService {
    private final GeminiApiClient geminiApiClient;

    @Autowired
    public GeminiApiService(GeminiApiClient geminiApiClient) {
        this.geminiApiClient = geminiApiClient;
    }

    public String generateBlogPost(String userTopic) {
        String modelId = "gemini-2.5-pro";
        String completePrompt = String.format("Bạn là một chuyên gia viết blog. Hãy viết một bài blog hấp dẫn " +
                        "và chi tiết về chủ đề: '%s'. Bài viết cần có cấu trúc rõ ràng.",
                userTopic);

        String rawApiResponse = this.geminiApiClient.generateContent(modelId, completePrompt);

        if (rawApiResponse == null) {
            return "Xin lỗi, đã có lỗi xảy ra khi tạo nội dung bài viết.";
        }

        return rawApiResponse;
    }
}
