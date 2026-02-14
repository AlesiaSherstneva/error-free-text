package by.senla.errorfreetext.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

/**
 * Response data transfer object for successful task creation.
 * Contains the unique identifier of the created task.
 */
@Data
@Builder
public class TaskCreatedResponseDto {
    private UUID id;
}