package by.senla.errorfreetext.client;

import by.senla.errorfreetext.converter.YandexSpellerRequestConverter;
import by.senla.errorfreetext.exception.YandexApiException;
import by.senla.errorfreetext.model.dto.YandexSpellerRequestDto;
import by.senla.errorfreetext.model.dto.YandexSpellerResponseDto;
import by.senla.errorfreetext.util.Constant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
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

        try {
            return restClient.post()
                    .uri(Constant.YANDEX_SPELLER_URI)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(queryString)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (HttpClientErrorException ex) {
            throw new YandexApiException(
                    Constant.INVALID_API_REQUEST_EXC_MESSAGE.formatted(ex.getMessage())
            );
        } catch (HttpServerErrorException ex) {
            throw new YandexApiException(
                    Constant.API_IS_UNAVAILABLE_EXC_MESSAGE.formatted(ex.getMessage())
            );
        } catch (ResourceAccessException ex) {
            throw new YandexApiException(
                    Constant.FAILED_TO_CONNECT_API_EXC_MESSAGE.formatted(ex.getMessage())
            );
        } catch (Exception ex) {
            throw new YandexApiException(
                    Constant.UNEXPECTED_API_ERROR_EXC_MESSAGE.formatted(ex.getMessage())
            );
        }
    }
}