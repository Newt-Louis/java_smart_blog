package ngoc.connect_gemini_api.controller;

import ngoc.connect_gemini_api.service.PostService;
import ngoc.connect_gemini_api.service.AuthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
public class HomePageController {
    PostService postService;
    AuthService authService;
    public HomePageController(PostService postService, AuthService authService) {
        this.authService = authService;
        this.postService = postService;
    }
    @GetMapping("/")
    public String indexPage(Model model) {
        model.addAttribute("welcomeMessage", "Chào mừng đến với Blog Thông Minh!");
        return "index";
    }

    @GetMapping("/homepage")
    public String homePage(Model model){
        model.addAttribute("username", "Guest");
        return "homepage";
    }
}
