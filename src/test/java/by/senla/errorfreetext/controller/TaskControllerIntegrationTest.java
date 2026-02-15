package by.senla.errorfreetext.controller;

import by.senla.errorfreetext.model.dto.TaskRequestDto;
import by.senla.errorfreetext.model.dto.enums.ErrorCode;
import by.senla.errorfreetext.model.entity.Task;
import by.senla.errorfreetext.model.entity.enums.Language;
import by.senla.errorfreetext.model.entity.enums.Status;
import by.senla.errorfreetext.repository.TaskRepository;
import by.senla.errorfreetext.util.Constant;
import by.senla.errorfreetext.util.TestConstant;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@ActiveProfiles("test")
@Import(Constant.class)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class TaskControllerIntegrationTest {
    @Container
    protected static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @DynamicPropertySource
    protected static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @MockitoSpyBean
    private TaskRepository taskRepository;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    private static final String BASE_URL = "/tasks";

    @Test
    void createTaskSuccessfullyIntegrationTest() throws Exception {
        TaskRequestDto request = TaskRequestDto.builder()
                .text("Hello world")
                .language(Language.EN)
                .build();

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath(TestConstant.JSON_PATH_ID).exists()
                );
    }

    @Test
    void createTaskWithEmptyTextIntegrationTest() throws Exception {
        TaskRequestDto request = TaskRequestDto.builder()
                .language(Language.EN)
                .build();

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath(TestConstant.JSON_PATH_ERROR_MESSAGE).value(containsString("Text cannot be empty")),
                        jsonPath(TestConstant.JSON_PATH_EXC_ERROR_CODE).value(ErrorCode.VALIDATION_FAILED.getCode()),
                        jsonPath(TestConstant.JSON_PATH_EXC_TIMESTAMP).exists(),
                        jsonPath(TestConstant.JSON_PATH_EXC_PATH).value(BASE_URL)
                );
    }

    @Test
    void createTaskWithTwoCharsTextIntegrationTest() throws Exception {
        TaskRequestDto request = TaskRequestDto.builder()
                .text("ab")
                .language(Language.EN)
                .build();

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath(TestConstant.JSON_PATH_ERROR_MESSAGE)
                                .value(containsString("Text must contain at least 3 characters")),
                        jsonPath(TestConstant.JSON_PATH_EXC_ERROR_CODE).value(ErrorCode.VALIDATION_FAILED.getCode()),
                        jsonPath(TestConstant.JSON_PATH_EXC_TIMESTAMP).exists(),
                        jsonPath(TestConstant.JSON_PATH_EXC_PATH).value(BASE_URL)
                );
    }

    @Test
    void createTaskWithEmptyLanguageIntegrationTest() throws Exception {
        TaskRequestDto request = TaskRequestDto.builder()
                .text("Hello world")
                .build();

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath(TestConstant.JSON_PATH_ERROR_MESSAGE)
                                .value(containsString("Language parameter is required")),
                        jsonPath(TestConstant.JSON_PATH_EXC_ERROR_CODE).value(ErrorCode.VALIDATION_FAILED.getCode()),
                        jsonPath(TestConstant.JSON_PATH_EXC_TIMESTAMP).exists(),
                        jsonPath(TestConstant.JSON_PATH_EXC_PATH).value(BASE_URL)
                );
    }

    @Test
    void createTaskWithWrongLanguageIntegrationTest() throws Exception {
        String request = "{\"text\": \"Hello world\", \"language\": \"BY\"}";

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath(TestConstant.JSON_PATH_ERROR_MESSAGE).value(containsString("Invalid language code")),
                        jsonPath(TestConstant.JSON_PATH_EXC_ERROR_CODE).value(ErrorCode.INVALID_LANGUAGE.getCode()),
                        jsonPath(TestConstant.JSON_PATH_EXC_TIMESTAMP).exists(),
                        jsonPath(TestConstant.JSON_PATH_EXC_PATH).value(BASE_URL)
                );
    }

    @Test
    void createTaskWhenMessageNotReadableIntegrationTest() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath(TestConstant.JSON_PATH_ERROR_MESSAGE).value(containsString("Invalid request format")),
                        jsonPath(TestConstant.JSON_PATH_EXC_ERROR_CODE).value(ErrorCode.INVALID_REQUEST_FORMAT.getCode()),
                        jsonPath(TestConstant.JSON_PATH_EXC_TIMESTAMP).exists(),
                        jsonPath(TestConstant.JSON_PATH_EXC_PATH).value(BASE_URL)
                );
    }

    @Test
    void getTaskResultWhenTaskStatusIsPendingTest() throws Exception {
        Task pendingTask = Task.builder()
                .originalText("Original text")
                .language(Language.RU)
                .status(Status.PENDING)
                .build();
        Task taskInDb = taskRepository.save(pendingTask);

        String url = String.format("%s/%s", BASE_URL, taskInDb.getId());

        mockMvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath(TestConstant.JSON_PATH_STATUS).value(Status.PENDING.name())
                );

        taskRepository.delete(taskInDb);
    }

    @Test
    void getTaskResultWhenTaskStatusIsCompletedTest() throws Exception {
        String correctedText = "Corrected text";

        Task completedTask = Task.builder()
                .originalText("Original text")
                .correctedText(correctedText)
                .language(Language.EN)
                .status(Status.COMPLETED)
                .build();
        Task taskInDb = taskRepository.save(completedTask);

        String url = String.format("%s/%s", BASE_URL, taskInDb.getId());

        mockMvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath(TestConstant.JSON_PATH_CORRECTED_TEXT).value(correctedText),
                        jsonPath(TestConstant.JSON_PATH_STATUS).value(Status.COMPLETED.name())
                );

        taskRepository.delete(taskInDb);
    }

    @Test
    void getTaskResultWhenTaskStatusIsFailedTest() throws Exception {
        String errorMessage = "Some error was thrown by API";

        Task failedTask = Task.builder()
                .originalText("Original text")
                .language(Language.RU)
                .status(Status.FAILED)
                .errorMessage(errorMessage)
                .build();
        Task taskInDb = taskRepository.save(failedTask);

        String url = String.format("%s/%s", BASE_URL, taskInDb.getId());

        mockMvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath(TestConstant.JSON_PATH_STATUS).value(Status.FAILED.name()),
                        jsonPath(TestConstant.JSON_PATH_ERROR_MESSAGE).value(errorMessage)
                );

        taskRepository.delete(taskInDb);
    }

    @Test
    void getTaskResultWhenTaskNotFoundTest() throws Exception {
        UUID taskId = UUID.randomUUID();
        String url = String.format("%s/%s", BASE_URL, taskId);

        mockMvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath(TestConstant.JSON_PATH_ERROR_MESSAGE)
                                .value(Constant.TASK_NOT_FOUND_EXC_MESSAGE.formatted(taskId)),
                        jsonPath(TestConstant.JSON_PATH_EXC_ERROR_CODE).value(ErrorCode.TASK_NOT_FOUND.getCode()),
                        jsonPath(TestConstant.JSON_PATH_EXC_TIMESTAMP).exists(),
                        jsonPath(TestConstant.JSON_PATH_EXC_PATH).value(url)
                );
    }

    @Test
    void getTaskResultWhenDatabaseIsUnavailableTest() throws Exception {
        UUID taskId = UUID.randomUUID();
        String url = String.format("%s/%s", BASE_URL, taskId);

        String dbErrorMessage = "Database access error";

        doThrow(new DataAccessException(dbErrorMessage) {}).when(taskRepository).getTaskById(taskId);

        mockMvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isInternalServerError(),
                        jsonPath(TestConstant.JSON_PATH_ERROR_MESSAGE)
                                .value(containsString(dbErrorMessage)),
                        jsonPath(TestConstant.JSON_PATH_EXC_ERROR_CODE).value(ErrorCode.DATABASE_ERROR.getCode()),
                        jsonPath(TestConstant.JSON_PATH_EXC_TIMESTAMP).exists(),
                        jsonPath(TestConstant.JSON_PATH_EXC_PATH).value(url)
                );
    }

    @Test
    void getTaskResultWhenUnexpectedExceptionIsThrownTest() throws Exception {
        UUID taskId = UUID.randomUUID();
        String url = String.format("%s/%s", BASE_URL, taskId);

        String unexpectedErrorMessage = "Something went wrong";

        doThrow(new RuntimeException(unexpectedErrorMessage) {}).when(taskRepository).getTaskById(taskId);

        mockMvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isInternalServerError(),
                        jsonPath(TestConstant.JSON_PATH_ERROR_MESSAGE)
                                .value(containsString(unexpectedErrorMessage)),
                        jsonPath(TestConstant.JSON_PATH_EXC_ERROR_CODE).value(ErrorCode.INTERNAL_ERROR.getCode()),
                        jsonPath(TestConstant.JSON_PATH_EXC_TIMESTAMP).exists(),
                        jsonPath(TestConstant.JSON_PATH_EXC_PATH).value(url)
                );
    }
}