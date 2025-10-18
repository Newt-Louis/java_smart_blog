package ngoc.connect_gemini_api.service;

import ngoc.connect_gemini_api.adapter.GeminiApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GeminiApiService {
    private final GeminiApiClient geminiApiClient;
    private final MarkdownConverterService markdownConverterService;

    @Autowired
    public GeminiApiService(GeminiApiClient geminiApiClient,MarkdownConverterService markdownConverterService) {
        this.geminiApiClient = geminiApiClient;
        this.markdownConverterService = markdownConverterService;
    }

    public String generateBlogPost(String userTopic) {
        String modelId = "gemini-2.5-pro";
        String completePrompt = String.format("Bạn là một chuyên gia viết blog. Hãy viết một bài blog hấp dẫn " +
                        "và chi tiết về chủ đề: '%s'. "+
                        "Bài viết cần có cấu trúc rõ ràng, bao gồm tiêu đề chính, các đề mục con, danh sách và nhấn mạnh các từ khóa quan trọng. "+
                        "Hãy định dạng TOÀN BỘ câu trả lời của bạn bằng Markdown. " +
                        "Sử dụng '#' cho tiêu đề, '-' cho danh sách, và '**' cho các từ in đậm.",
                userTopic);
        String rawApiResponse = this.geminiApiClient.generateContent(modelId, completePrompt);

        if (rawApiResponse == null) {
            return "Xin lỗi, đã có lỗi xảy ra khi tạo nội dung bài viết.";
        }

        return markdownConverterService.markdownToHtml(rawApiResponse);
    }
}
