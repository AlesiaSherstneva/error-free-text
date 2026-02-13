package by.senla.errorfreetext.model.dto;

import by.senla.errorfreetext.util.Constant;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class YandexSpellerRequestDto {
    private static final int IGNORE_DIGITS_OPTION = 2;
    private static final int IGNORE_URLS_OPTION = 4;

    private String text;
    private String lang;
    private int options;

    public static class YandexSpellerRequestDtoBuilder {
        public YandexSpellerRequestDto build() {
            if (text.matches(Constant.CONTAINS_DIGIT_PATTERN)) {
                options += IGNORE_DIGITS_OPTION;
            }

            if (text.matches(Constant.CONTAINS_URL_PATTERN)) {
                options += IGNORE_URLS_OPTION;
            }

            return new YandexSpellerRequestDto(text, lang, options);
        }
    }
}