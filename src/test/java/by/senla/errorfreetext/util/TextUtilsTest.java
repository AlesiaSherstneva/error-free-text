package by.senla.errorfreetext.util;

import by.senla.errorfreetext.model.dto.YandexSpellerResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(classes = TextUtils.class)
class TextUtilsTest {
    @Autowired
    private TextUtils textUtils;

    @Test
    void splitTextWhenTextIsShorterThanMaxTextSizeTest() {
        String shortText = "Short text";

        List<String> textParts = textUtils.splitText(shortText);

        assertThat(textParts).isNotNull().isNotEmpty().hasSize(1);
        assertThat(textParts.get(0)).isNotNull().isEqualTo(shortText);
    }

    @Test
    void splitTextWhenTextIsEqualToMaxTextSizeTest() {
        String textWith25chars = "Text with 25 characters..";

        List<String> textParts = textUtils.splitText(textWith25chars);

        assertThat(textParts).isNotNull().isNotEmpty().hasSize(1);
        assertThat(textParts.get(0)).isNotNull().isEqualTo(textWith25chars);
    }

    @Test
    void splitTextWhenTextIsLongerThanMaxTextSizeTest() {
        String longText = "Long text which is longer than 25 characters";

        List<String> textParts = textUtils.splitText(longText);

        String expectedFirstPart = longText.substring(0, longText.lastIndexOf(' ', 25));

        assertThat(textParts).isNotNull().isNotEmpty().hasSize(2);
        assertThat(textParts.get(0)).isNotNull().isEqualTo(expectedFirstPart);
    }

    @Test
    void calculateOptionsWhenTextDoesNotContainDigitsAndUrlsTest() {
        String textWithoutDigitsOrUrls = "Text without digits or urls";

        int calculatedOptions = textUtils.calculateOptions(textWithoutDigitsOrUrls);

        assertThat(calculatedOptions).isEqualTo(0);
    }

    @Test
    void calculateOptionsWhenTextContainsDigitsTest() {
        String textWithDigits = "Text with digits: 123";

        int calculatedOptions = textUtils.calculateOptions(textWithDigits);

        assertThat(calculatedOptions).isEqualTo(2);
    }

    @Test
    void calculateOptionsWhenTextContainsUrlTest() {
        String textWithUrl = "Text with url http://www.google.com";

        int calculatedOptions = textUtils.calculateOptions(textWithUrl);

        assertThat(calculatedOptions).isEqualTo(4);
    }

    @Test
    void calculateOptionsWhenTextContainsDigitsAndUrlTest() {
        String textWithDigitsOrUrls = "Text with digits and url: http://www.google.com 321";

        int calculatedOptions = textUtils.calculateOptions(textWithDigitsOrUrls);

        assertThat(calculatedOptions).isEqualTo(6);
    }

    @Test
    void applyCorrectionsWhenCorrectionsListIsEmptyTest() {
        String originalPart = "Some text";
        List<String> textParts = List.of(originalPart);

        List<List<YandexSpellerResponseDto>> corrections = List.of(List.of());

        String resultOfCorrections = textUtils.applyCorrections(textParts, corrections);

        assertThat(resultOfCorrections).isNotBlank().isEqualTo(originalPart);
    }

    @Test
    void applyCorrectionsWhenSingleCorrectionInOnePartTest() {
        String originalPart = "Some text with eror";
        List<String> textParts = List.of(originalPart);

        YandexSpellerResponseDto correction = YandexSpellerResponseDto.builder()
                .pos(15)
                .len(4)
                .s(List.of("error"))
                .build();
        List<List<YandexSpellerResponseDto>> corrections = List.of(List.of(correction));

        String resultOfCorrections = textUtils.applyCorrections(textParts, corrections);

        assertThat(resultOfCorrections).isNotBlank().isEqualTo("Some text with error");
    }

    @Test
    void applyCorrectionsWhenMultipleCorrectionsInOnePartTest() {
        String originalPart = "Ths text has two erors";
        List<String> textParts = List.of(originalPart);

        YandexSpellerResponseDto correction1 = YandexSpellerResponseDto.builder()
                .pos(0)
                .len(3)
                .s(List.of("This"))
                .build();

        YandexSpellerResponseDto correction2 = YandexSpellerResponseDto.builder()
                .pos(17)
                .len(5)
                .s(List.of("errors"))
                .build();
        List<List<YandexSpellerResponseDto>> corrections = List.of(List.of(correction1, correction2));

        String result = textUtils.applyCorrections(textParts, corrections);

        assertThat(result).isNotBlank().isEqualTo("This text has two errors");
    }

    @Test
    void applyCorrectionsWhenCorrectionsInMultiplePartsTest() {
        List<String> textParts = List.of("First part with eror ", "Second part with eror");

        YandexSpellerResponseDto correction1 = YandexSpellerResponseDto.builder()
                .pos(16)
                .len(4)
                .s(List.of("error"))
                .build();
        YandexSpellerResponseDto correction2 = YandexSpellerResponseDto.builder()
                .pos(17)
                .len(4)
                .s(List.of("error"))
                .build();
        List<List<YandexSpellerResponseDto>> apiResponse = List.of(
                List.of(correction1),
                List.of(correction2)
        );

        String result = textUtils.applyCorrections(textParts, apiResponse);

        assertThat(result).isNotBlank().isEqualTo("First part with error Second part with error");
    }
}