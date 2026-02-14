package by.senla.errorfreetext.util;

import by.senla.errorfreetext.model.dto.YandexSpellerResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for text processing operations.
 * Provides methods for splitting text, calculating API options, and applying corrections.
 */
@Slf4j
@Component
public class TextUtils {
    private static final int IGNORE_DIGITS_OPTION = 2;
    private static final int IGNORE_URLS_OPTION = 4;

    @Value("${yandex.api.max-text-size}")
    private int maxTextSize;

    /**
     * Splits text into parts that fit within Yandex API size limit.
     * Attempts to split at word boundaries when possible.
     *
     * @param text the original text to split
     * @return list of text parts, each not exceeding maxTextSize
     */
    public List<String> splitText(String text) {
        if (text.length() <= maxTextSize) {
            return List.of(text);
        }

        List<String> textParts = new ArrayList<>();
        int beginOfPart = 0;

        while (beginOfPart < text.length()) {
            int endOfPart = findSplitPosition(text, beginOfPart);
            textParts.add(text.substring(beginOfPart, endOfPart));
            beginOfPart = endOfPart;
        }

        return textParts;
    }

    private int findSplitPosition(String text, int beginOfPart) {
        int endOfPart = Math.min(beginOfPart + maxTextSize, text.length());

        if (!(endOfPart == text.length()) && !(text.charAt(endOfPart) == ' ')) {
            int beginOfNextWord = text.lastIndexOf(' ', endOfPart) + 1;

            if (beginOfNextWord > beginOfPart) {
                return beginOfNextWord;
            }
        }

        return endOfPart;
    }

    /**
     * Calculates Yandex Speller API options based on text content.
     * Enables IGNORE_DIGITS if text contains numbers.
     * Enables IGNORE_URLS if text contains URLs.
     *
     * @param text the text to analyze
     * @return bitmask of enabled options
     */
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

    /**
     * Applies corrections from Yandex API to the original text parts.
     * Combines corrected parts back into a single text.
     *
     * @param parts the original text parts
     * @param corrections list of corrections for each part
     * @return fully corrected text
     */
    public String applyCorrections(List<String> parts, List<List<YandexSpellerResponseDto>> corrections) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < parts.size(); i++) {
            result.append(correctPart(parts.get(i), corrections.get(i)));
        }

        return result.toString();
    }

    private StringBuilder correctPart(String part, List<YandexSpellerResponseDto> corrections) {
        StringBuilder correctedPart = new StringBuilder(part);

        if (corrections == null || corrections.isEmpty()) {
            return correctedPart;
        }

        corrections.stream()
                .sorted((a, b) -> b.getPos() - a.getPos())
                .filter(c -> c.getS() != null && !c.getS().isEmpty())
                .forEach(c -> correctedPart.replace(c.getPos(), c.getPos() + c.getLen(), c.getS().get(0)));

        return correctedPart;
    }
}