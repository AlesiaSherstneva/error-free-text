package by.senla.errorfreetext.service;

import by.senla.errorfreetext.exception.InvalidRequestException;
import by.senla.errorfreetext.model.dto.TaskCreatedResponseDto;
import by.senla.errorfreetext.model.dto.TaskRequestDto;
import by.senla.errorfreetext.model.dto.TaskResultResponseDto;
import by.senla.errorfreetext.model.dto.mapper.TaskMapperImpl;
import by.senla.errorfreetext.model.entity.Task;
import by.senla.errorfreetext.model.entity.enums.Language;
import by.senla.errorfreetext.model.entity.enums.Status;
import by.senla.errorfreetext.repository.TaskRepository;
import by.senla.errorfreetext.service.impl.TaskServiceImpl;
import by.senla.errorfreetext.util.Constant;
import by.senla.errorfreetext.util.TextUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@Import(Constant.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {TaskServiceImpl.class, TaskMapperImpl.class, TextUtils.class})
class TaskServiceTest {
    @MockitoBean
    private TaskRepository taskRepository;

    @Autowired
    private TaskService taskService;

    @Test
    void createTaskSuccessfullyTest() {
        TaskRequestDto request = TaskRequestDto.builder()
                .text("Valid text")
                .language(Language.EN)
                .build();
        Task createdTask = Task.builder()
                .id(UUID.randomUUID())
                .build();

        when(taskRepository.save(any(Task.class))).thenReturn(createdTask);

        TaskCreatedResponseDto response = taskService.createTask(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull().isEqualTo(createdTask.getId());

        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void createTaskWhenTextWithOnlySpecialCharsTest() {
        TaskRequestDto request = TaskRequestDto.builder()
                .text("!@#$%")
                .language(Language.EN)
                .build();

        assertThatThrownBy(() -> taskService.createTask(request))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage(Constant.ONLY_SPECIALS_EXC_MESSAGE);

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void getTaskResultWhenTaskIsPendingTest() {
        UUID taskId = UUID.randomUUID();
        Task taskInDb = Task.builder()
                .id(taskId)
                .status(Status.PENDING)
                .build();

        when(taskRepository.getTaskById(taskId)).thenReturn(Optional.of(taskInDb));

        TaskResultResponseDto response = taskService.getTaskResult(taskId);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(Status.PENDING);

        verify(taskRepository, times(1)).getTaskById(taskId);
    }

    @Test
    void getTaskResultWhenTaskIsCompletedTest() {
        UUID taskId = UUID.randomUUID();
        String correctedText = "Corrected text";

        Task taskInDb = Task.builder()
                .id(taskId)
                .correctedText(correctedText)
                .status(Status.COMPLETED)
                .build();

        when(taskRepository.getTaskById(taskId)).thenReturn(Optional.of(taskInDb));

        TaskResultResponseDto response = taskService.getTaskResult(taskId);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isNotNull().isEqualTo(Status.COMPLETED);
        assertThat(response.getCorrectedText()).isNotBlank().isEqualTo(correctedText);

        verify(taskRepository, times(1)).getTaskById(taskId);
    }

    @Test
    void getTaskResultWhenTaskIsFailedTest() {
        UUID taskId = UUID.randomUUID();
        String errorMessage = "Some error was occurred";

        Task taskInDb = Task.builder()
                .id(taskId)
                .status(Status.FAILED)
                .errorMessage(errorMessage)
                .build();

        when(taskRepository.getTaskById(taskId)).thenReturn(Optional.of(taskInDb));

        TaskResultResponseDto response = taskService.getTaskResult(taskId);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isNotNull().isEqualTo(Status.FAILED);
        assertThat(response.getErrorMessage()).isNotBlank().isEqualTo(errorMessage);

        verify(taskRepository, times(1)).getTaskById(taskId);
    }

    @Test
    void getTaskResultWhenTaskNotFoundTest() {
        UUID taskId = UUID.randomUUID();

        when(taskRepository.getTaskById(taskId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTaskResult(taskId))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage(Constant.TASK_NOT_FOUND_EXC_MESSAGE.formatted(taskId));

        verify(taskRepository, times(1)).getTaskById(taskId);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(taskRepository);
    }
}