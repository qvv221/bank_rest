package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * Запрос на создание карты.
 */
@Getter
@Setter
@Schema(description = "Запрос на создание карты")
public class CreateCardRequest {

    @Schema(description = "Username владельца (кому создать карту)")
    @NotBlank
    private String username;

    @Schema(description = "Полный номер карты")
    @NotBlank
    @Pattern(regexp = "^\\d{12}$", message = "Номер карты должен содержать 12 цифр")
    private String number;

    @Schema(description = "Срок действия (MM/YY)")
    @NotBlank
    @Pattern(regexp = "^(0[1-9]|1[0-2])/\\d{2}$", message = "Срок действия в формате MM/YY")
    private String validityPeriod;

    @Schema(description = "Баланс")
    @NotNull
    @Min(0)
    private Long balance;
}

