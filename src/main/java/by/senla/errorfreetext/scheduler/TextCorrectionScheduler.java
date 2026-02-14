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

@Slf4j
@Component
@RequiredArgsConstructor
public class TextCorrectionScheduler {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final YandexSpellerClient spellerClient;
    private final TextUtils textUtils;

    @Scheduled(fixedDelayString = "${scheduler.fixed-delay}")
    public void processPendingTasks() {
        List<Task> pendingTasks = taskRepository.findTasksByStatus(Status.PENDING);

        if (pendingTasks.isEmpty()) {
            return;
        }

        pendingTasks.forEach(this::processTask);
    }

    private void processTask(Task task) {
        try {
            YandexSpellerRequestDto request = taskMapper.toYandexRequestDto(task);
            List<List<YandexSpellerResponseDto>> apiResponse = spellerClient.checkTexts(request);

            String correctedText = textUtils.applyCorrections(request.getTextParts(), apiResponse);

            task.setCorrectedText(correctedText);
            task.setStatus(Status.COMPLETED);
        } catch (YandexApiException ex) {
            task.setStatus(Status.FAILED);
            task.setErrorMessage(ex.getMessage());
        } finally {
            taskRepository.save(task);
        }
    }
}