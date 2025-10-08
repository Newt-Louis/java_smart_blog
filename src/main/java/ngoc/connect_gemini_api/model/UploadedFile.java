package ngoc.connect_gemini_api.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class UploadedFile {
    private Integer id;
    private String filename;
    private String originalFilename;
    private String filePath;
    private String mimeType;
    private Long fileSize;
    private Integer userId;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
