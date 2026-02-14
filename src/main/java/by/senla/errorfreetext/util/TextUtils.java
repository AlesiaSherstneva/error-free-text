package by.senla.errorfreetext.util;

import by.senla.errorfreetext.model.dto.YandexSpellerResponseDto;
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