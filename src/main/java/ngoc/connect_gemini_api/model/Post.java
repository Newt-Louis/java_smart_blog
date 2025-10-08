package ngoc.connect_gemini_api.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Post {
    private int id;
    private int userId;
    private String title;
    private String content;
    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
