package by.senla.errorfreetext.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Request data transfer object for Yandex Speller API.
 * Contains text parts to be checked and spell checking parameters.
 */
@Data
@Builder
public class YandexSpellerRequestDto {
    private List<String> textParts;
    private String lang;
    private int options;
}