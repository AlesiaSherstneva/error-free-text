package by.senla.errorfreetext.model.entity.enums;

import by.senla.errorfreetext.exception.InvalidRequestException;
import by.senla.errorfreetext.model.dto.enums.ErrorCode;
import by.senla.errorfreetext.util.Constant;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

/**
 * Supported languages for text correction.
 */
public enum Language {
    RU, EN;

    /**
     * Creates a Language enum from string value.
     * Used by Jackson for deserializing JSON requests.
     *
     * @param value the language string (case-insensitive, accepts "ru" or "en")
     * @return matching Language enum
     * @throws InvalidRequestException if value doesn't match any language
     */
    @JsonCreator
    public static Language fromString(String value) {
        return Arrays.stream(Language.values())
                .filter(language -> language.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new InvalidRequestException(
                        Constant.INVALID_LANG_EXC_MESSAGE.formatted(value), ErrorCode.INVALID_LANGUAGE
                ));
    }
}