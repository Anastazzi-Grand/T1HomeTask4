package com.example.auth.service;

import com.example.auth.dto.UserRegistrationDto;
import com.example.auth.entity.User;
import com.example.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Регистрация нового пользователя
     */
    public void register(UserRegistrationDto dto) {
        // Проверка уникальности
        if (userRepository.findByLogin(dto.getLogin()) != null) {
            throw new RuntimeException("Логин уже занят");
        }
        if (userRepository.findByEmail(dto.getEmail()) != null) {
            throw new RuntimeException("Email уже занят");
        }

        User user = new User();
        user.setLogin(dto.getLogin());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        // Установка ролей
        List<String> roles = dto.getRoles();
        if (roles == null || roles.isEmpty()) {
            roles = List.of("guest"); // роль по умолчанию
        }

        // Проверка допустимых ролей
        for (String role : roles) {
            if (!List.of("admin", "premium_user", "guest").contains(role)) {
                throw new RuntimeException("Недопустимая роль: " + role);
            }
        }

        user.setRoles(roles); // Теперь просто передаём List<String>

        userRepository.save(user);
    }
}