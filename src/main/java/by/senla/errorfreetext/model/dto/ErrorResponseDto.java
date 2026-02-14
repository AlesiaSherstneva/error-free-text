package by.senla.errorfreetext.model.dto;

import by.senla.errorfreetext.model.dto.enums.ErrorCode;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ErrorResponseDto {
    private String errorMessage;
    private ErrorCode errorCode;

    @Builder.Default
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime timestamp = LocalDateTime.now();

    private String path;
}