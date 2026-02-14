package by.senla.errorfreetext.service.impl;

import by.senla.errorfreetext.exception.InvalidRequestException;
import by.senla.errorfreetext.model.dto.TaskCreatedResponseDto;
import by.senla.errorfreetext.model.dto.TaskRequestDto;
import by.senla.errorfreetext.model.dto.TaskResultResponseDto;
import by.senla.errorfreetext.model.dto.enums.ErrorCode;
import by.senla.errorfreetext.model.dto.mapper.TaskMapper;
import by.senla.errorfreetext.model.entity.Task;
import by.senla.errorfreetext.repository.TaskRepository;
import by.senla.errorfreetext.service.TaskService;
import by.senla.errorfreetext.util.Constant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Implementation of {@link TaskService} interface.
 * Provides business logic for task creation and retrieval.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    /**
     * {@inheritDoc}
     */
    @Override
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
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
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