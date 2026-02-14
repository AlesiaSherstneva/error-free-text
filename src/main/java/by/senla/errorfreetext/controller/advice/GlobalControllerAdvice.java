package by.senla.errorfreetext.controller.advice;

import by.senla.errorfreetext.exception.InvalidRequestException;
import by.senla.errorfreetext.model.dto.ErrorResponseDto;
import by.senla.errorfreetext.model.dto.enums.ErrorCode;
import by.senla.errorfreetext.util.Constant;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * Global exception handler for the application.
 * Processes exceptions thrown by controllers and returns standardized error responses.
 */
@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    /**
     * Handles custom InvalidRequestException thrown when client request is invalid.
     *
     * @param ex the InvalidRequestException
     * @param request the HTTP request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidRequestException(InvalidRequestException ex,
                                                                          HttpServletRequest request) {
        log.warn("Invalid request: {} for path: {}", ex.getMessage(), request.getRequestURI());

        ErrorResponseDto response = ErrorResponseDto.builder()
                .errorMessage(ex.getMessage())
                .errorCode(ex.getErrorCode())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(ex.getErrorCode().getHttpStatus()).body(response);
    }

    /**
     * Handles validation exceptions from Spring's @Valid annotation.
     *
     * @param ex the MethodArgumentNotValidException containing validation errors
     * @param request the HTTP request
     * @return ResponseEntity with validation error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(MethodArgumentNotValidException ex,
                                                                      HttpServletRequest request) {
        List<String> errorDetails = ex.getBindingResult()
                .getFieldErrors().stream()
                .map(error -> Constant.ERROR_MESSAGE_PART_FORMAT.formatted(error.getField(), error.getDefaultMessage()))
                .toList();

        log.warn("Validation failed: {} for path: {}", errorDetails, request.getRequestURI());

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .errorMessage(Constant.VALIDATION_FAILED_EXC_MESSAGE.formatted(errorDetails))
                .errorCode(ErrorCode.VALIDATION_FAILED)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(ErrorCode.VALIDATION_FAILED.getHttpStatus()).body(errorResponse);
    }

    /**
     * Handles data integrity violations.
     *
     * @param ex the DataIntegrityViolationException
     * @param request the HTTP request
     * @return ResponseEntity with database error details
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleDataViolationException(DataIntegrityViolationException ex,
                                                                         HttpServletRequest request) {
        log.error("Data integrity violation: {} for path: {}", ex.getMessage(), request.getRequestURI(), ex);

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .errorMessage(Constant.DATABASE_ERROR_EXC_MESSAGE.formatted(ex.getMessage()))
                .errorCode(ErrorCode.DATA_INTEGRITY_VIOLATION)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(ErrorCode.DATA_INTEGRITY_VIOLATION.getHttpStatus()).body(errorResponse);
    }

    /**
     * Handles general database access exceptions.
     *
     * @param ex the DataAccessException
     * @param request the HTTP request
     * @return ResponseEntity with database error details
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponseDto> handleDataAccessException(DataAccessException ex,
                                                                      HttpServletRequest request) {
        log.error("Database access error: {} for path: {}", ex.getMessage(), request.getRequestURI(), ex);

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .errorMessage(Constant.DATABASE_ERROR_EXC_MESSAGE.formatted(ex.getMessage()))
                .errorCode(ErrorCode.DATABASE_ERROR)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(ErrorCode.DATABASE_ERROR.getHttpStatus()).body(errorResponse);
    }

    /**
     * Handles all unhandled exceptions.
     *
     * @param ex the Exception
     * @param request the HTTP request
     * @return ResponseEntity with internal server error details
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleUnexpectedException(Exception ex,
                                                                  HttpServletRequest request) {
        log.error("Unexpected error occurred: {} for path: {}", ex.getMessage(), request.getRequestURI(), ex);

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .errorMessage(Constant.INTERNAL_SERVER_ERROR_EXC_MESSAGE.formatted(ex.getMessage()))
                .errorCode(ErrorCode.INTERNAL_ERROR)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(ErrorCode.INTERNAL_ERROR.getHttpStatus()).body(errorResponse);
    }
}