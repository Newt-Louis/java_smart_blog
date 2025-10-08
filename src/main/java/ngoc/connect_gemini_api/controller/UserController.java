package ngoc.connect_gemini_api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @GetMapping("/user")
    public String welcome() {
        return "Chào mừng đến với Smart Blog API! Server đang hoạt động tốt.";
    }
}
