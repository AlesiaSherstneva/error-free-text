package by.senla.errorfreetext.service;

import by.senla.errorfreetext.model.dto.TaskRequestDto;
import by.senla.errorfreetext.model.dto.TaskResponseDto;
import by.senla.errorfreetext.model.dto.mapper.TaskMapper;
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
    private final TaskMapper taskMapper;

    @Transactional
    public TaskResponseDto createTask(TaskRequestDto request) {
        if (request.getText().matches(Constant.ONLY_SPECIALS_PATTERN)) {
            throw new IllegalArgumentException(Constant.ONLY_SPECIALS_EXC_MESSAGE);
        }

        Task newTask = taskMapper.toEntity(request);

        Task createdTask = taskRepository.save(newTask);

        return taskMapper.toDto(createdTask);
    }
}