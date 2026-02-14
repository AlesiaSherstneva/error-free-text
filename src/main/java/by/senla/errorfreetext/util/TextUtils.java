package by.senla.errorfreetext.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TextUtils {
    private static final int IGNORE_DIGITS_OPTION = 2;
    private static final int IGNORE_URLS_OPTION = 4;

    @Value("${yandex.api.max-text-size}")
    private int maxTextSize;

    public List<String> splitText(String text) {
        List<String> textParts = new ArrayList<>();

        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + maxTextSize, text.length());

            if (end < text.length()) {
                int lastSpace = text.lastIndexOf(' ', end);

                if (lastSpace > start) {
                    end = lastSpace;
                }

                int lastPeriod = text.lastIndexOf(". ", end);

                if (lastPeriod > start && lastPeriod > lastSpace) {
                    end = lastPeriod + 2;
                }
            }

            textParts.add(text.substring(start, end).trim());
            start = end;
        }

        return textParts;
    }

    public int calculateOptions(String text) {
        int options = 0;

        if (text.matches(Constant.CONTAINS_DIGIT_PATTERN)) {
            options += IGNORE_DIGITS_OPTION;
        }

        if (text.matches(Constant.CONTAINS_URL_PATTERN)) {
            options += IGNORE_URLS_OPTION;
        }

        return options;
    }
}