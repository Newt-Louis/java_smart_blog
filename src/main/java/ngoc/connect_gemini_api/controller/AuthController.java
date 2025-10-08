package ngoc.connect_gemini_api.controller;

import jakarta.servlet.http.HttpSession;
import ngoc.connect_gemini_api.dto.request.auth.LoginRequest;
import ngoc.connect_gemini_api.dto.request.auth.RegisterRequest;
import ngoc.connect_gemini_api.model.User;
import ngoc.connect_gemini_api.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    @PostMapping("/login")
    public String login(LoginRequest request, RedirectAttributes redirectAttributes, HttpSession session){
        User loggedInUser = authService.authenticate(request);
        if (loggedInUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid username or password");
        } else {
            session.setAttribute("loggedInUser",loggedInUser);
            redirectAttributes.addFlashAttribute("logginSuccess", "Login successful!");
        }
        return "redirect:/homepage";
    }

    @PostMapping("/register")
    public String register(RegisterRequest request, RedirectAttributes redirectAttributes){
        try {
            User registeredUser = authService.registerUser(request);
            redirectAttributes.addFlashAttribute("newUser",registeredUser);
            redirectAttributes.addFlashAttribute("registerSuccess","User registered successfully!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage",e.getMessage());
        }
        return "redirect:/homepage";
    }

    @GetMapping("/logout")
    public String logout(RedirectAttributes redirectAttributes,HttpSession session){
        session.removeAttribute("loggedInUser");
        return "redirect:/homepage";
    }
}
