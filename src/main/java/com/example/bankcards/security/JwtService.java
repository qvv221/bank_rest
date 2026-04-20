package com.example.bankcards.security;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {

    /**
     * Извлекает имя пользователя из JWT.
     */
    String extractUserName(String token);

    /**
     * Генерирует JWT для указанного пользователя.
     */
    String generateToken(UserDetails userDetails);

    /**
     * Проверяет корректность JWT и соответствие пользователю.
     */
    boolean isTokenValid(String token, UserDetails userDetails);
}
