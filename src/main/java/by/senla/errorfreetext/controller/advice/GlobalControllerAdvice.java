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

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidRequestException(InvalidRequestException ex,
                                                                          HttpServletRequest request) {
        ErrorResponseDto response = ErrorResponseDto.builder()
                .errorMessage(ex.getMessage())
                .errorCode(ex.getErrorCode())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(ex.getErrorCode().getHttpStatus()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(MethodArgumentNotValidException ex,
                                                                      HttpServletRequest request) {
        List<String> errorDetails = ex.getBindingResult()
                .getFieldErrors().stream()
                .map(error -> Constant.ERROR_MESSAGE_PART_FORMAT.formatted(error.getField(), error.getDefaultMessage()))
                .toList();

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .errorMessage(Constant.VALIDATION_FAILED_EXC_MESSAGE.formatted(errorDetails))
                .errorCode(ErrorCode.VALIDATION_FAILED)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(ErrorCode.VALIDATION_FAILED.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleDataViolationException(DataIntegrityViolationException ex,
                                                                         HttpServletRequest request) {
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .errorMessage(Constant.DATABASE_ERROR_EXC_MESSAGE.formatted(ex.getMessage()))
                .errorCode(ErrorCode.DATA_INTEGRITY_VIOLATION)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(ErrorCode.DATA_INTEGRITY_VIOLATION.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponseDto> handleDataAccessException(DataAccessException ex,
                                                                      HttpServletRequest request) {

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .errorMessage(Constant.DATABASE_ERROR_EXC_MESSAGE.formatted(ex.getMessage()))
                .errorCode(ErrorCode.DATABASE_ERROR)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(ErrorCode.DATABASE_ERROR.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleUnexpectedException(Exception ex,
                                                                  HttpServletRequest request) {
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .errorMessage(Constant.INTERNAL_SERVER_ERROR_EXC_MESSAGE.formatted(ex.getMessage()))
                .errorCode(ErrorCode.INTERNAL_ERROR)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(ErrorCode.INTERNAL_ERROR.getHttpStatus()).body(errorResponse);
    }
}