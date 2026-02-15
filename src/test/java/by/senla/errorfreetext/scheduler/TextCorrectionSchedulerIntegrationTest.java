package by.senla.errorfreetext.scheduler;

import by.senla.errorfreetext.client.YandexSpellerClient;
import by.senla.errorfreetext.exception.YandexApiException;
import by.senla.errorfreetext.model.dto.YandexSpellerRequestDto;
import by.senla.errorfreetext.model.dto.YandexSpellerResponseDto;
import by.senla.errorfreetext.model.entity.Task;
import by.senla.errorfreetext.model.entity.enums.Language;
import by.senla.errorfreetext.model.entity.enums.Status;
import by.senla.errorfreetext.repository.TaskRepository;
import by.senla.errorfreetext.util.TextUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class TextCorrectionSchedulerIntegrationTest {
    @Container
    protected static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @DynamicPropertySource
    protected static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @MockitoBean
    private YandexSpellerClient yandexSpellerClient;

    @MockitoBean
    private TextUtils textUtils;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TextCorrectionScheduler textCorrectionScheduler;

    private Task pendingTask;
    private UUID taskId;

    @BeforeEach
    void setUp() {
        pendingTask = Task.builder()
                .originalText("Original text")
                .language(Language.EN)
                .status(Status.PENDING)
                .build();

        taskId = taskRepository.save(pendingTask).getId();
    }

    @Test
    void processPendingTasksSuccessfullyIntegrationTest() {
        YandexSpellerResponseDto response = YandexSpellerResponseDto.builder().build();
        String correctedText = "Corrected text";

        when(yandexSpellerClient.checkTexts(any(YandexSpellerRequestDto.class)))
                .thenReturn(List.of(List.of(response)));
        when(textUtils.applyCorrections(anyList(), anyList())).thenReturn(correctedText);

        textCorrectionScheduler.processPendingTasks();

        Task completedTask = taskRepository.getTaskById(taskId).orElse(null);

        assertThat(completedTask).isNotNull();
        assertThat(completedTask.getStatus()).isNotNull().isEqualTo(Status.COMPLETED);
        assertThat(completedTask.getCorrectedText()).isNotBlank().isEqualTo(correctedText);
    }

    @Test
    void processPendingTasksWhenApiThrewExceptionIntegrationTest() {
        String errorMessage = "API temporarily unavailable";

        when(yandexSpellerClient.checkTexts(any(YandexSpellerRequestDto.class)))
                .thenThrow(new YandexApiException(errorMessage));

        textCorrectionScheduler.processPendingTasks();

        Task failedTask = taskRepository.getTaskById(taskId).orElse(null);

        assertThat(failedTask).isNotNull();
        assertThat(failedTask.getStatus()).isNotNull().isEqualTo(Status.FAILED);
        assertThat(failedTask.getErrorMessage()).isNotNull().contains(errorMessage);
        assertThat(failedTask.getCorrectedText()).isNull();
    }

    @AfterEach
    void tearDown() {
        taskRepository.delete(pendingTask);
    }
}