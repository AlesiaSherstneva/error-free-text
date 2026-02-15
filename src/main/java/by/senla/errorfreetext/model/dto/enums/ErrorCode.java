package by.senla.errorfreetext.model.dto.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Application error codes with corresponding HTTP statuses.
 * Each error code is a unique string identifier for specific error types.
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    TASK_NOT_FOUND(40401, HttpStatus.NOT_FOUND),

    VALIDATION_FAILED(40001, HttpStatus.BAD_REQUEST),
    INVALID_LANGUAGE(40002, HttpStatus.BAD_REQUEST),
    INVALID_REQUEST_FORMAT(40003, HttpStatus.BAD_REQUEST),

    INTERNAL_ERROR(50001, HttpStatus.INTERNAL_SERVER_ERROR),
    DATABASE_ERROR(50002, HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final HttpStatus httpStatus;
}