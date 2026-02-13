package by.senla.errorfreetext.client;

import by.senla.errorfreetext.model.dto.YandexSpellerRequestDto;
import by.senla.errorfreetext.model.dto.YandexSpellerResponseDto;
import by.senla.errorfreetext.converter.YandexSpellerRequestConverter;
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

        return restClient.post()
                .uri("/checkTexts")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(requestConverter.convert(request))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }
}