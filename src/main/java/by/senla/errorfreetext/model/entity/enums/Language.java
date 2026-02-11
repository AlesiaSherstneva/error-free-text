package by.senla.errorfreetext.model.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Language {
    RU, EN;

    @JsonCreator
    public static Language fromString(String value) {
        if (value == null) {
            return null;
        }

        return Language.valueOf(value);
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}