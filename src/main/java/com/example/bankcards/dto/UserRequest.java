package com.example.bankcards.dto;

import com.example.bankcards.constant.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Создание и обновление пользователя.
 */
@Getter
@Setter
@Schema(description = "Создание и обновление пользователя")
public class UserRequest {

    public interface Create {
    }

    @Schema(description = "Имя пользователя")
    @NotBlank(message = "Имя пользователя не может быть пустым", groups = Create.class)
    private String username;

    @Schema(description = "Email")
    @NotBlank(message = "Email не может быть пустым", groups = Create.class)
    @Email(message = "Некорректный email")
    private String email;

    @Schema(description = "Пароль")
    @NotBlank(message = "Пароль не может быть пустым", groups = Create.class)
    private String password;

    @Schema(description = "Роль", allowableValues = {"ADMIN", "USER"})
    @NotNull(message = "Роль обязательна", groups = Create.class)
    private Role role;
}
