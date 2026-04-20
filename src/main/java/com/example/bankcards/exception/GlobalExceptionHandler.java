package com.example.bankcards.exception;

import com.example.bankcards.constant.BankRestErrorCode;
import com.example.bankcards.mapper.ProblemDetailMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ProblemDetailMapper problemDetailMapper;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex) {
        ResponseEntity<ProblemDetail> body = problemDetailMapper.responseFromValidation(ex);
        log.debug(body.getBody() != null ? body.getBody().getDetail() : null, ex);
        return body;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAccessDenied(AccessDeniedException ex) {
        log.warn(ex.getMessage(), ex);
        return problemDetailMapper.response(HttpStatus.FORBIDDEN, ex, BankRestErrorCode.FORBIDDEN);
    }

    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    public ResponseEntity<ProblemDetail> handleSignInFailure(AuthenticationException ex) {
        log.warn(ex.getMessage(), ex);
        return problemDetailMapper.response(HttpStatus.UNAUTHORIZED, "Неверное имя пользователя или пароль", BankRestErrorCode.UNAUTHORIZED);
    }

    @ExceptionHandler(BankRestRuntimeException.class)
    public ResponseEntity<ProblemDetail> handleBankRest(BankRestRuntimeException ex) {
        log.warn(ex.getMessage(), ex);
        return problemDetailMapper.response(ex.getHttpStatus(), ex, ex.getErrorCode());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn(ex.getMessage(), ex);
        return problemDetailMapper.response(HttpStatus.BAD_REQUEST, ex, BankRestErrorCode.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleOther(Exception ex) {
        log.error(ex.getMessage(), ex);
        return problemDetailMapper.response(BankRestErrorCode.INTERNAL_ERROR.getHttpStatus(), BankRestErrorCode.INTERNAL_ERROR);
    }
}
