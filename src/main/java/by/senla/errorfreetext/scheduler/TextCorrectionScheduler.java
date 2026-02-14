package by.senla.errorfreetext.scheduler;

import by.senla.errorfreetext.client.YandexSpellerClient;
import by.senla.errorfreetext.exception.YandexApiException;
import by.senla.errorfreetext.model.dto.YandexSpellerRequestDto;
import by.senla.errorfreetext.model.dto.YandexSpellerResponseDto;
import by.senla.errorfreetext.model.dto.mapper.TaskMapper;
import by.senla.errorfreetext.model.entity.Task;
import by.senla.errorfreetext.model.entity.enums.Status;
import by.senla.errorfreetext.repository.TaskRepository;
import by.senla.errorfreetext.util.TextUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Scheduler for processing pending text correction tasks.
 * Runs periodically to fetch pending tasks and correct them using Yandex Speller API.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TextCorrectionScheduler {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final YandexSpellerClient spellerClient;
    private final TextUtils textUtils;

    /**
     * Processes all pending tasks at fixed intervals.
     * Fetches task with PENDING status and processes each one individually.
     */
    @Scheduled(fixedDelayString = "${scheduler.fixed-delay}")
    public void processPendingTasks() {
        log.debug("Starting scheduled processing of pending tasks");

        List<Task> pendingTasks = taskRepository.findTasksByStatus(Status.PENDING);

        if (pendingTasks.isEmpty()) {
            log.debug("No pending tasks found");

            return;
        }

        log.info("Found {} pending tasks to process", pendingTasks.size());

        pendingTasks.forEach(this::processTask);
    }

    private void processTask(Task task) {
        log.debug("Processing task ID: {}", task.getId());

        try {
            YandexSpellerRequestDto request = taskMapper.toYandexRequestDto(task);
            List<List<YandexSpellerResponseDto>> apiResponse = spellerClient.checkTexts(request);

            String correctedText = textUtils.applyCorrections(request.getTextParts(), apiResponse);

            task.setCorrectedText(correctedText);
            task.setStatus(Status.COMPLETED);

            log.info("Task {} completed successfully", task.getId());
        } catch (YandexApiException ex) {
            log.error("Failed to process task {}: {}", task.getId(), ex.getMessage());

            task.setStatus(Status.FAILED);
            task.setErrorMessage(ex.getMessage());
        } finally {
            taskRepository.save(task);

            log.debug("Task {} saved with status: {}", task.getId(), task.getStatus());
        }
    }
}