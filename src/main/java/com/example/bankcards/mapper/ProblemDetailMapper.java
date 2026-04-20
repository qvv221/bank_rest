package com.example.bankcards.mapper;

import com.example.bankcards.constant.BankRestErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ProblemDetailMapper {

    private static final String CODE_PROPERTY = "code";

    public ResponseEntity<ProblemDetail> responseFromValidation(MethodArgumentNotValidException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(toProblemDetail(ex));
    }

    public ProblemDetail toProblemDetail(MethodArgumentNotValidException ex) {
        String detail = validationMessage(ex);
        BankRestErrorCode code = BankRestErrorCode.VALIDATION_ERROR;
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
        problemDetail.setTitle(code.name());
        problemDetail.setProperty(CODE_PROPERTY, code.name());
        return problemDetail;
    }

    public ResponseEntity<ProblemDetail> response(HttpStatus status, Throwable ex, BankRestErrorCode code) {
        String detail = StringUtils.hasText(ex.getMessage()) ? ex.getMessage() : code.getMessage();
        return ResponseEntity
                .status(status)
                .body(problemDetail(status, detail, code));
    }

    public ResponseEntity<ProblemDetail> response(HttpStatus status, BankRestErrorCode code) {
        return ResponseEntity
                .status(status)
                .body(problemDetail(status, code.getMessage(), code));
    }

    public ResponseEntity<ProblemDetail> response(HttpStatus status, String detail, BankRestErrorCode code) {
        return ResponseEntity
                .status(status)
                .body(problemDetail(status, detail, code));
    }

    private String validationMessage(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();

        String message = Stream.concat(
                        bindingResult.getFieldErrors().stream()
                                .map(FieldError::getDefaultMessage),

                        bindingResult.getGlobalErrors().stream()
                                .map(ObjectError::getDefaultMessage)
                )
                .filter(StringUtils::hasText)
                .distinct()
                .collect(Collectors.joining("; "));

        return StringUtils.hasText(message) ? message : BankRestErrorCode.VALIDATION_ERROR.getMessage();
    }

    private ProblemDetail problemDetail(HttpStatus status, String detail, BankRestErrorCode code) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(code.name());
        problemDetail.setProperty(CODE_PROPERTY, code.name());
        return problemDetail;
    }
}
