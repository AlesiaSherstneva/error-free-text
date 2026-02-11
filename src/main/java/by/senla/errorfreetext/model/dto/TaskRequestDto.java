package by.senla.errorfreetext.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskRequestDto {
    @NotBlank(message = "Text cannot be empty")
    @Size(min = 3, message = "Text must contain at least 3 characters")
    private String text;

    @NotBlank(message = "Language parameter is required")
    @Pattern(regexp = "^(EN|RU)$", message = "Language must be either EN or RU")
    private String language;
}