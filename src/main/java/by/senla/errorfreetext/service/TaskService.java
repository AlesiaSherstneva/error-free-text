package by.senla.errorfreetext.service;

import by.senla.errorfreetext.model.dto.TaskRequestDto;
import by.senla.errorfreetext.model.dto.TaskResponseDto;
import by.senla.errorfreetext.model.entity.Task;
import by.senla.errorfreetext.repository.TaskRepository;
import by.senla.errorfreetext.util.Constant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    @Transactional
    public TaskResponseDto createTask(TaskRequestDto request) {
        if (!request.getText().matches(Constant.TEXT_VALIDATION_PATTERN)) {
            throw new IllegalArgumentException("Text cannot contain only digits and special characters");
        }

        Task newTask = Task.builder()
                .originalText(request.getText())
                .language(request.getLanguage())
                .build();

        return null;
    }
}