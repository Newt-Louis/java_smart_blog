package ngoc.connect_gemini_api.adapter;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class GeminiApiClient {
    private static final Logger logger = LoggerFactory.getLogger(GeminiApiClient.class);

    private final Client geminiClient;

    @Autowired
    public GeminiApiClient(Client geminiClient) {
        this.geminiClient = geminiClient;
    }

    /**
     * Gửi yêu cầu đến Gemini API và trả về nội dung text.
     * @param modelId Tên model muốn sử dụng (vd: "gemini-2.5-flash").
     * @param completePrompt Prompt hoàn chỉnh đã được Service xử lý.
     * @return Nội dung text từ API, hoặc null nếu có lỗi.
     */
    public String generateContent(String modelId, String completePrompt) {
        try {
            logger.info("Sending request to Gemini model: {}", modelId);

            GenerateContentResponse response =
                    this.geminiClient.models.generateContent(modelId, completePrompt, null);

            logger.info("Successfully received response from Gemini.");
            return response.text();

        } catch (Exception e) {
            logger.error("Error calling Gemini API: {}", e.getMessage(), e);
            return null;
        }
    }
}