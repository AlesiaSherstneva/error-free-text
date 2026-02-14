package by.senla.errorfreetext.model.dto.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    TASK_NOT_FOUND("40401", HttpStatus.NOT_FOUND),

    VALIDATION_FAILED("40001", HttpStatus.BAD_REQUEST),
    INVALID_LANGUAGE("40002", HttpStatus.BAD_REQUEST),

    DATA_INTEGRITY_VIOLATION("40901", HttpStatus.CONFLICT),

    INTERNAL_ERROR("50001", HttpStatus.INTERNAL_SERVER_ERROR),
    DATABASE_ERROR("50002", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final HttpStatus httpStatus;

    @Override
    public String toString() {
        return code;
    }
}