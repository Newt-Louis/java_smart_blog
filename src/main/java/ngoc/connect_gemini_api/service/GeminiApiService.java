package ngoc.connect_gemini_api.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GeminiApiService {
    private final Client geminiClient;

    @Autowired
    public GeminiApiService(Client geminiClient) {
        this.geminiClient = geminiClient;
    }

    public String generateBlogPost(String userTopic) {
        String modelId = "gemini-2.5-flash";
        String prompt = String.format("Viết một bài blog chi tiết về chủ đề: '%s'", userTopic);

        GenerateContentResponse response =
                this.geminiClient.models.generateContent(modelId, prompt, null);

        return response.text();
    }
}
