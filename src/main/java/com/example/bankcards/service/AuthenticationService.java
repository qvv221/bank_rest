package com.example.bankcards.service;

import com.example.bankcards.dto.JwtAuthenticationResponse;
import com.example.bankcards.dto.SignInRequest;

public interface AuthenticationService {

    /**
     * Аутентифицирует пользователя и возвращает JWT.
     */
    JwtAuthenticationResponse signIn(SignInRequest request);
}
