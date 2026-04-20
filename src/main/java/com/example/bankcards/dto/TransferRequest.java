package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Запрос на перевод между своими картами.
 */
@Getter
@Setter
@Schema(description = "Запрос на перевод между своими картами")
public class TransferRequest {

    @Schema(description = "ID карты-источника")
    @NotNull
    private Long fromCardId;

    @Schema(description = "ID карты-получателя")
    @NotNull
    private Long toCardId;

    @Schema(description = "Сумма перевода")
    @NotNull
    @Min(value = 1)
    private Long amount;
}

