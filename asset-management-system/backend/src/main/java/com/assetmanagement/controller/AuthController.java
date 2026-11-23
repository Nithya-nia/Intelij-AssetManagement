package com.assetmanagement.controller;

import com.assetmanagement.dto.AuthRequest;
import com.assetmanagement.dto.AuthResponse;
import com.assetmanagement.model.Admin;
import com.assetmanagement.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        Admin admin = authService.getCurrentAdmin(authentication.getName());
        Map<String, Object> response = new HashMap<>();
        response.put("username", admin.getUsername());
        response.put("fullName", admin.getFullName());
        response.put("email", admin.getEmail());
        response.put("role", admin.getRole());
        return ResponseEntity.ok(response);
    }
}
