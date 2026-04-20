package com.example.bankcards.exception;

import com.example.bankcards.constant.BankRestErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Единое runtime-исключение приложения.
 */
@Getter
public class BankRestRuntimeException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final BankRestErrorCode errorCode;

    public BankRestRuntimeException(BankRestErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = errorCode.getHttpStatus();
    }
}
