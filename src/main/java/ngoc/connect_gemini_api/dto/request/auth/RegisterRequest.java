package ngoc.connect_gemini_api.dto.request.auth;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private String phone;
}
