package by.senla.errorfreetext.converter;

import by.senla.errorfreetext.model.dto.YandexSpellerRequestDto;
import by.senla.errorfreetext.util.Constant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class YandexSpellerRequestConverter {
    public String convert(YandexSpellerRequestDto request) {
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance();

        request.getTextParts().forEach(text -> builder.queryParam("text", text));

        builder.queryParam(Constant.QUERY_PARAM_LANG, request.getLang());
        builder.queryParam(Constant.QUERY_PARAM_OPTIONS, request.getOptions());

        return builder.build()
                .encode(StandardCharsets.UTF_8)
                .getQuery();
    }
}