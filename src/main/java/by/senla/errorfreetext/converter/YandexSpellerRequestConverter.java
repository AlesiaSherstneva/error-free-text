package by.senla.errorfreetext.converter;

import by.senla.errorfreetext.model.dto.YandexSpellerRequestDto;
import by.senla.errorfreetext.util.TextSplitter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class YandexSpellerRequestConverter {
    private final TextSplitter textSplitter;

    @Value("${yandex.api.max-text-size}")
    private int maxTextSize;

    public String convert(YandexSpellerRequestDto request) {
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance();

        if (request.getText().length() <= maxTextSize) {
            builder.queryParam("text", request.getText());
        } else {
            textSplitter.splitText(request.getText()).forEach(text -> builder.queryParam("text", text));
        }

        builder.queryParam("lang", request.getLang());
        builder.queryParam("options", request.getOptions());

        return builder.build()
                .encode(StandardCharsets.UTF_8)
                .getQuery();
    }
}