package by.senla.errorfreetext.model.dto;

import by.senla.errorfreetext.model.entity.enums.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

/**
 * Response data transfer object for task result retrieval.
 * Contains task status and either corrected text or error message.
 * Fields with null values are excluded from JSON response.
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskResultResponseDto {
    private Status status;
    private String correctedText;
    private String errorMessage;
}