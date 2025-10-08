package ngoc.connect_gemini_api.dto.request.post;

import lombok.Data;

@Data
public class UpdatePostRequest {
    private int id;
    private String title;
    private String content;
}
