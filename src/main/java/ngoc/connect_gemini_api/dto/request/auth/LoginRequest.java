package ngoc.connect_gemini_api.dto.request.auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;

    public void setLogin_username(String username) {
        this.username = username;
    }
    public void setLogin_password(String password) {
        this.password = password;
    }
}
