package com.example.bankcards.dto;

import com.example.bankcards.constant.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * Пользователь.
 */
@Getter
@Setter
@Schema(description = "Пользователь")
public class UserDto {

    @Schema(description = "Идентификатор")
    private Long id;

    @Schema(description = "Имя пользователя")
    private String username;

    @Schema(description = "Email")
    private String email;

    @Schema(description = "Роль")
    private Role role;
}
