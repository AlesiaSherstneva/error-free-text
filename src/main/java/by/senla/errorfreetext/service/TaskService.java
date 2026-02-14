package by.senla.errorfreetext.service;

import by.senla.errorfreetext.exception.InvalidRequestException;
import by.senla.errorfreetext.model.dto.TaskCreatedResponseDto;
import by.senla.errorfreetext.model.dto.TaskRequestDto;
import by.senla.errorfreetext.model.dto.TaskResultResponseDto;

import java.util.UUID;

/**
 * Interface for managing text correction tasks.
 */
public interface TaskService {
    /**
     * Creates a new text correction task.
     * Validates that the input text contains at least one alphanumeric character.
     *
     * @param request the task request containing text and language
     * @return response DTO containing the created task ID
     * @throws InvalidRequestException if the text contains only special characters
     */
    TaskCreatedResponseDto createTask(TaskRequestDto request);

    /**
     * Retrieves the result of a text correction task by its ID.
     *
     * @param id the UUID of the task
     * @return response DTO containing task status, corrected text or error message
     * @throws InvalidRequestException if task with given ID is not found
     */
    TaskResultResponseDto getTaskResult(UUID id);
}