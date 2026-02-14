package by.senla.errorfreetext.exception;

import by.senla.errorfreetext.model.dto.enums.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Exception thrown when a client request is invalid.
 * Used for business rule violations (text contains only special characters,
 * invalid language, task not found).
 */
@Getter
@RequiredArgsConstructor
public class InvalidRequestException extends RuntimeException {
    private final ErrorCode errorCode;

    public InvalidRequestException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}