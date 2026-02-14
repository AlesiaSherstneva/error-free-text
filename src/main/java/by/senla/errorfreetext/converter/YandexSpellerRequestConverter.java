package by.senla.errorfreetext.converter;

import by.senla.errorfreetext.model.dto.YandexSpellerRequestDto;
import by.senla.errorfreetext.util.Constant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;

/**
 * Converter for transforming YandexSpellerRequestDto into URL-encoded query string.
 * Formats multiple text parameters, appends language and options.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class YandexSpellerRequestConverter {

    /**
     * Converts a YandexSpellerRequestDto to a URL-encoded query string.
     *
     * @param request the request DTO containing text parts, language and options
     * @return URL-encoded query string ready for API request body
     */
    public String convert(YandexSpellerRequestDto request) {
        log.debug("Converting request to query string. Text parts: {}, Language: {}, Options: {}",
                request.getTextParts().size(), request.getLang(), request.getOptions());

        UriComponentsBuilder builder = UriComponentsBuilder.newInstance();

        request.getTextParts().forEach(text -> builder.queryParam("text", text));

        builder.queryParam(Constant.QUERY_PARAM_LANG, request.getLang());
        builder.queryParam(Constant.QUERY_PARAM_OPTIONS, request.getOptions());

        return builder.build()
                .encode(StandardCharsets.UTF_8)
                .getQuery();
    }
}