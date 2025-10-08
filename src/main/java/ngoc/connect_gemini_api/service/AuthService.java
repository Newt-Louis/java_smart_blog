package ngoc.connect_gemini_api.service;

import ngoc.connect_gemini_api.dto.request.auth.LoginRequest;
import ngoc.connect_gemini_api.dto.request.auth.RegisterRequest;
import ngoc.connect_gemini_api.model.User;
import ngoc.connect_gemini_api.repository.UserMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private final UserMapper userMapper;
    public FileSystemStorageService storageService;

    public AuthService(UserMapper userMapper, FileSystemStorageService storageService) {
        this.userMapper = userMapper;
        this.storageService = storageService;
    }

    public User authenticate(LoginRequest request){
        Optional<User> userOptional = userMapper.findByUsername(request.getUsername());
        if (userOptional.isPresent()) {
            User userFromDB = userOptional.get();
            if (userFromDB.getPassword().equals(request.getPassword())) {
                return userFromDB;
            }
        }
        return null;
    }

    public User registerUser(RegisterRequest request){
        if (userMapper.findByUsername(request.getUsername()).isPresent()){
            throw new IllegalStateException("Username is already in use");
        }
        if (userMapper.findByEmail(request.getEmail()).isPresent()){
            throw new IllegalStateException("Email is already in use");
        }
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(request.getPassword());
        newUser.setPhone(request.getPhone());
        newUser.setRole("Default");
        User savedUser = userMapper.insert(newUser);
        if (savedUser != null) {
            storageService.createUserDirectory(savedUser.getId());
        }
        return savedUser;
    }
}
