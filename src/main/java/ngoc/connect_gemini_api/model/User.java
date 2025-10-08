package ngoc.connect_gemini_api.model;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class User {
    private int id;
    private String code;
    private String fullName;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String role;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
