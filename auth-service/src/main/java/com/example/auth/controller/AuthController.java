package com.example.auth.controller;

import com.example.auth.entity.User;
import com.example.auth.service.AuthService;
import com.example.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        log.info("Registering user: {}", user.getEmail());
        return ResponseEntity.ok(authService.register(user));
    }

    @PostMapping("/token")
    public ResponseEntity<String> getToken(@RequestBody User user) {
        log.info("Generating token for user: {}", user.getEmail());
        // Simple token generation for testing
        // In real app, verify password first!
        return ResponseEntity.ok(jwtUtil.generateToken(user.getEmail()));
    }

    @GetMapping("/validate")
    public String validateToken(@RequestParam("token") String token) {
        jwtUtil.validateToken(token);
        return "Token is valid";
    }

    @GetMapping
    public ResponseEntity<String> getUser() {
        return ResponseEntity.ok("Hi auth work!");
    }
}
