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

/**
 * Client for interacting with Yandex Speller API.
 * Handles HTTP communication and converts exceptions to custom YandexApiException.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class YandexSpellerClient {
    private final RestClient restClient;
    private final YandexSpellerRequestConverter requestConverter;

    /**
     * Sends text parts to Yandex Speller API for spell checking.
     *
     * @param request the request DTO containing text parts, language and options
     * @return list of spell checking results for each text part
     * @throws YandexApiException if any error occurs during API communication
     */
    public List<List<YandexSpellerResponseDto>> checkTexts(YandexSpellerRequestDto request) {
        log.debug("Preparing request to Yandex Speller API. Text parts: {}, Language: {}",
                request.getTextParts().size(), request.getLang());

        String queryString = requestConverter.convert(request);

        log.trace("Request body: {}", queryString);

        try {
            List<List<YandexSpellerResponseDto>> apiResponse = restClient.post()
                    .uri(Constant.YANDEX_SPELLER_URI)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(queryString)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });

            log.debug("Successfully received response from Yandex Speller API");

            return apiResponse;
        } catch (HttpClientErrorException ex) {
            log.error("Yandex API returned client error: {}", ex.getMessage());

            throw new YandexApiException(
                    Constant.INVALID_API_REQUEST_EXC_MESSAGE.formatted(ex.getMessage())
            );
        } catch (HttpServerErrorException ex) {
            log.error("Yandex API returned server error: {}", ex.getMessage());

            throw new YandexApiException(
                    Constant.API_IS_UNAVAILABLE_EXC_MESSAGE.formatted(ex.getMessage())
            );
        } catch (ResourceAccessException ex) {
            log.error("Failed to connect to Yandex API: {}", ex.getMessage());

            throw new YandexApiException(
                    Constant.FAILED_TO_CONNECT_API_EXC_MESSAGE.formatted(ex.getMessage())
            );
        } catch (Exception ex) {
            log.error("Unexpected error while calling Yandex API: {}", ex.getMessage(), ex);

            throw new YandexApiException(
                    Constant.UNEXPECTED_API_ERROR_EXC_MESSAGE.formatted(ex.getMessage())
            );
        }
    }
}