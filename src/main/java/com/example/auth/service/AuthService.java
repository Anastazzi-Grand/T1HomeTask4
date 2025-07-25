package com.example.auth.service;

import com.example.auth.dto.UserRegistrationDto;
import com.example.auth.entity.User;
import com.example.auth.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserService userService;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public AuthService(UserService userService,
                       TokenService tokenService,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Регистрация нового пользователя
     */
    public void register(UserRegistrationDto dto) {
        userService.register(dto);
    }

    /**
     * Вход пользователя (логин и пароль)
     * @return токен при успешной аутентификации
     */
    public String login(String login, String password) {
        User user = userRepository.findByLogin(login);
        if (user == null) {
            throw new RuntimeException("Пользователь не найден");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Неверный пароль");
        }

        return tokenService.generateToken(user);
    }

    /**
     * Обновление токена (по старому токену)
     */
    public String refreshToken(String oldToken) {
        User user = tokenService.getUserByToken(oldToken);
        if (user == null) {
            throw new RuntimeException("Токен недействителен");
        }
        return tokenService.refreshToken(oldToken, user);
    }

    /**
     * Выход (отзыв токена)
     */
    public void logout(String token) {
        tokenService.revokeToken(token);
    }
}