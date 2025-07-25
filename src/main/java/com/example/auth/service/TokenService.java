package com.example.auth.service;

import com.example.auth.entity.Token;
import com.example.auth.entity.User;
import com.example.auth.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class TokenService {

    private static final int TOKEN_TTL_MINUTES = 30; // Время жизни токена — 30 минут

    private final TokenRepository tokenRepository;

    @Autowired
    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    /**
     * Генерация нового токена для пользователя
     */
    public String generateToken(User user) {
        // Удаляем старые токены пользователя (очистка)
        tokenRepository.deleteByUserId(user.getId());

        String token = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(TOKEN_TTL_MINUTES);

        Token tokenEntity = new Token();
        tokenEntity.setToken(token);
        tokenEntity.setUser(user);
        tokenEntity.setExpiresAt(expiresAt);

        tokenRepository.save(tokenEntity);
        return token;
    }

    /**
     * Проверка, действителен ли токен (существует и не истёк)
     */
    public boolean isValid(String token) {
        return tokenRepository.findByToken(token)
                .filter(t -> t.getExpiresAt().isAfter(LocalDateTime.now()))
                .isPresent();
    }

    /**
     * Получить пользователя по токену (если токен валиден)
     */
    public User getUserByToken(String token) {
        return tokenRepository.findByToken(token)
                .filter(t -> t.getExpiresAt().isAfter(LocalDateTime.now()))
                .map(Token::getUser)
                .orElse(null);
    }

    /**
     * Отзыв токена (удаление)
     */
    public void revokeToken(String token) {
        tokenRepository.deleteByToken(token);
    }

    /**
     * Обновление токена (по старому токену)
     */
    public String refreshToken(String oldToken, User user) {
        if (isValid(oldToken)) {
            revokeToken(oldToken);
            return generateToken(user);
        }
        throw new RuntimeException("Токен недействителен или истёк");
    }
}