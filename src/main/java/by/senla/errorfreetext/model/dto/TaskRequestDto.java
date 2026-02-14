package by.senla.errorfreetext.model.dto;

import by.senla.errorfreetext.model.entity.enums.Language;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request data transfer object for creating a new text correction task.
 * Contains the text to be corrected and the target language.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequestDto {
    @NotBlank(message = "Text cannot be empty")
    @Size(min = 3, message = "Text must contain at least 3 characters")
    private String text;

    @NotNull(message = "Language parameter is required")
    private Language language;
}