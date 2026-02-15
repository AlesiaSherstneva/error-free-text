package by.senla.errorfreetext.converter;

import by.senla.errorfreetext.model.dto.YandexSpellerRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = YandexSpellerRequestConverter.class)
class YandexSpellerRequestConverterTest {
    private static final String EXPECTED_SINGLE_QUERY_PATTERN = "text=%s&lang=%s&options=%d";
    private static final String EXPECTED_DOUBLE_QUERY_PATTERN = "text=%s&text=%s&lang=%s&options=%d";

    @Autowired
    private YandexSpellerRequestConverter converter;

    @Test
    void convertSingleTextPartWithoutOptions() {
        String textPart = "Hello world";

        YandexSpellerRequestDto request = YandexSpellerRequestDto.builder()
                .textParts(List.of(textPart))
                .lang("en")
                .options(0)
                .build();

        String query = converter.convert(request);

        String expectedQuery = EXPECTED_SINGLE_QUERY_PATTERN.formatted(
                textPart.replaceAll(" ", "%20"),
                request.getLang().toLowerCase(),
                request.getOptions()
        );

        assertThat(query).isNotBlank().isEqualTo(expectedQuery);
    }

    @Test
    void convertDoubleTextPartWithOptions() {
        String firstPart = "First part", secondPart = "Second part";

        YandexSpellerRequestDto request = YandexSpellerRequestDto.builder()
                .textParts(List.of(firstPart, secondPart))
                .lang("en")
                .options(6)
                .build();

        String query = converter.convert(request);

        String expectedQuery = EXPECTED_DOUBLE_QUERY_PATTERN.formatted(
                firstPart.replaceAll(" ", "%20"),
                secondPart.replaceAll(" ", "%20"),
                request.getLang().toLowerCase(),
                request.getOptions()
        );

        assertThat(query).isNotBlank().isEqualTo(expectedQuery);
    }
}