package com.example.auth.controller;

import com.example.auth.dto.UserRegistrationDto;
import com.example.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Регистрация нового пользователя
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegistrationDto dto) {
        try {
            authService.register(dto);
            return ResponseEntity.ok("Пользователь успешно зарегистрирован");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Вход (логин)
     * Ожидает: login и password в теле запроса
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String login = request.get("login");
        String password = request.get("password");

        if (login == null || password == null) {
            return ResponseEntity.badRequest().body("Требуются login и password");
        }

        try {
            String token = authService.login(login, password);
            return ResponseEntity.ok(Map.of("token", token));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    /**
     * Обновление токена (по старому токену)
     * Позволяет получить новый токен без логина/пароля
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String oldToken = request.get("token");

        if (oldToken == null) {
            return ResponseEntity.badRequest().body("Требуется токен");
        }

        try {
            String newToken = authService.refreshToken(oldToken);
            return ResponseEntity.ok(Map.of("token", newToken));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    /**
     * Выход (отзыв токена)
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Требуется заголовок Authorization: Bearer <token>");
        }

        String token = authHeader.substring(7); // "Bearer " — 7 символов

        try {
            authService.logout(token);
            return ResponseEntity.ok("Токен отозван");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}