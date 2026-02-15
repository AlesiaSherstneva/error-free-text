package by.senla.errorfreetext.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Response data transfer object from Yandex Speller API for a single error.
 * Exactly matches the JSON response structure of Yandex Speller API.
 */
@Data
@Builder
public class YandexSpellerResponseDto {
    private int code;
    private int pos;
    private int row;
    private int col;
    private int len;
    private String word;
    private List<String> s;
}