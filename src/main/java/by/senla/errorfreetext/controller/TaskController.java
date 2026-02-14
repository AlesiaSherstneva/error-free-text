package by.senla.errorfreetext.controller;

import by.senla.errorfreetext.model.dto.TaskCreatedResponseDto;
import by.senla.errorfreetext.model.dto.TaskRequestDto;
import by.senla.errorfreetext.model.dto.TaskResultResponseDto;
import by.senla.errorfreetext.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST controller for managing test correction task.
 * Provides endpoints for creating task and retrieving correction result.
 */
@Slf4j
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    /**
     * Creates a new correction task.
     *
     * @param request the task request to create
     * @return ResponseEntity with created task id
     */
    @PostMapping
    public ResponseEntity<TaskCreatedResponseDto> createTask(@RequestBody @Valid TaskRequestDto request) {
        log.info("Received request to create new task. Language: {}, Text length: {}",
                request.getLanguage(), request.getText().length());

        TaskCreatedResponseDto response = taskService.createTask(request);

        log.info("Task created successfully with ID: {}", response.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves the result of a text correction task by its ID
     *
     * @param id the UUID of the task
     * @return ResponseEntity with task result (status, corrected text or error message)
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskResultResponseDto> getTask(@PathVariable UUID id) {
        log.info("Received request to get task result for ID: {}", id);

        TaskResultResponseDto response = taskService.getTaskResult(id);

        log.debug("Task {} retrieved with status: {}", id, response.getStatus());

        return ResponseEntity.ok(response);
    }
}