package by.senla.errorfreetext.service;

import by.senla.errorfreetext.exception.InvalidRequestException;
import by.senla.errorfreetext.model.dto.TaskCreatedResponseDto;
import by.senla.errorfreetext.model.dto.TaskRequestDto;
import by.senla.errorfreetext.model.dto.TaskResultResponseDto;
import by.senla.errorfreetext.model.dto.enums.ErrorCode;
import by.senla.errorfreetext.model.dto.mapper.TaskMapper;
import by.senla.errorfreetext.model.entity.Task;
import by.senla.errorfreetext.repository.TaskRepository;
import by.senla.errorfreetext.util.Constant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service class for managing text correction tasks.
 * Handles task creation, retrieval, and business logic validation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    /**
     * Creates a new text correction task.
     * Validates that the input text contains at least one alphanumeric character.
     *
     * @param request the task request containing text and language
     * @return response DTO containing the created task ID
     * @throws InvalidRequestException if the text contains only special characters
     */
    @Transactional
    public TaskCreatedResponseDto createTask(TaskRequestDto request) {
        log.debug("Creating new task with language: {}", request.getLanguage());

        if (request.getText().matches(Constant.ONLY_SPECIALS_PATTERN)) {
            log.warn("Task creation failed: text contains only special characters");

            throw new InvalidRequestException(
                    Constant.ONLY_SPECIALS_EXC_MESSAGE, ErrorCode.VALIDATION_FAILED
            );
        }

        Task newTask = taskMapper.toEntity(request);
        Task createdTask = taskRepository.save(newTask);

        log.info("Task created successfully with ID: {}", createdTask.getId());

        return taskMapper.toCreatedResponseDto(createdTask);
    }

    /**
     * Retrieves the result of a text correction task by its ID.
     *
     * @param id the UUID of the task
     * @return response DTO containing task status, corrected text or error message
     * @throws InvalidRequestException if task with given ID is not found
     */
    @Transactional
    public TaskResultResponseDto getTaskResult(UUID id) {
        log.debug("Fetching task result for ID: {}", id);

        Task taskInDb = taskRepository.getTaskById(id)
                .orElseThrow(() -> {
                    log.warn("Task not found with ID: {}", id);

                    return new InvalidRequestException(
                            Constant.TASK_NOT_FOUND_EXC_MESSAGE.formatted(id),
                            ErrorCode.TASK_NOT_FOUND
                    );
                });

        log.debug("Task {} found with status: {}", id, taskInDb.getStatus());

        return taskMapper.toResultResponseDto(taskInDb);
    }
}