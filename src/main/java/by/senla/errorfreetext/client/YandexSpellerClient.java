package by.senla.errorfreetext.client;

import by.senla.errorfreetext.model.dto.YandexSpellerRequestDto;
import by.senla.errorfreetext.model.dto.YandexSpellerResponseDto;
import by.senla.errorfreetext.converter.YandexSpellerRequestConverter;
import by.senla.errorfreetext.util.Constant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class YandexSpellerClient {
    private final RestClient restClient;
    private final YandexSpellerRequestConverter requestConverter;

    public List<List<YandexSpellerResponseDto>> checkTexts(YandexSpellerRequestDto request) {
        String queryString = requestConverter.convert(request);

        return restClient.post()
                .uri(Constant.YANDEX_SPELLER_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(queryString)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }
}