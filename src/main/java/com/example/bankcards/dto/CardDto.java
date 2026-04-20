package com.example.bankcards.dto;

import com.example.bankcards.constant.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * Карта.
 */
@Getter
@Setter
@Schema(description = "Карта")
public class CardDto {

    @Schema(description = "Идентификатор карты")
    private Long id;

    @Schema(description = "Номер карты")
    private String number;

    @Schema(description = "Владелец")
    private String owner;

    @Schema(description = "Срок действия")
    private String validityPeriod;

    @Schema(description = "Статус")
    private Status status;

    @Schema(description = "Баланс")
    private Long balance;
}
