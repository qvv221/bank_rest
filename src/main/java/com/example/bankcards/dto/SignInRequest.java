package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Запрос на аутентификацию.
 */
@Getter
@Setter
public class SignInRequest {

    @Schema(description = "Имя пользователя")
    @NotBlank(message = "Имя пользователя не может быть пустыми")
    private String username;

    @Schema(description = "Пароль")
    @NotBlank(message = "Пароль не может быть пустыми")
    private String password;
}
