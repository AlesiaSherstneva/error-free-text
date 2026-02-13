package by.senla.errorfreetext.model.entity.enums;

import by.senla.errorfreetext.util.Constant;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

public enum Language {
    RU, EN;

    @JsonCreator
    public static Language fromString(String value) {
        return Arrays.stream(Language.values())
                .filter(language -> language.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        Constant.INVALID_LANG_EXC_MESSAGE.formatted(value)
                ));
    }
}