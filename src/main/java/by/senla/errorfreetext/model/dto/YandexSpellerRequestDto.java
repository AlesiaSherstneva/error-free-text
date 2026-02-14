package by.senla.errorfreetext.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class YandexSpellerRequestDto {
    private List<String> textParts;
    private String lang;
    private int options;
}