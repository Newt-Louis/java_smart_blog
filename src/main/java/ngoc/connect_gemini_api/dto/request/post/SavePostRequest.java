package ngoc.connect_gemini_api.dto.request.post;

import lombok.Data;

@Data
public class SavePostRequest {
    private String title;
    private String content;
}
