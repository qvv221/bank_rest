package com.example.bankcards.constant;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * HTTP-статусы.
 */
@Getter
public enum BankRestErrorCode {

    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Ошибка валидации"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Некорректный запрос"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "Не найдено"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "Недостаточно прав"),
    CONFLICT(HttpStatus.CONFLICT, "Конфликт данных"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Ошибка аутентификации"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера");

    private final HttpStatus httpStatus;
    private final String message;

    BankRestErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public String code() {
        return name();
    }
}
