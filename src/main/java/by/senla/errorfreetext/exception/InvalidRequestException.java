package by.senla.errorfreetext.exception;

import by.senla.errorfreetext.model.dto.enums.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class InvalidRequestException extends RuntimeException {
    private final ErrorCode errorCode;

    public InvalidRequestException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}