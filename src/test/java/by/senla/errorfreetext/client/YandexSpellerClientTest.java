package by.senla.errorfreetext.client;

import by.senla.errorfreetext.converter.YandexSpellerRequestConverter;
import by.senla.errorfreetext.exception.YandexApiException;
import by.senla.errorfreetext.model.dto.YandexSpellerRequestDto;
import by.senla.errorfreetext.model.dto.YandexSpellerResponseDto;
import by.senla.errorfreetext.util.Constant;
import by.senla.errorfreetext.util.TestConstant;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.MediaType;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestClient;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.badRequest;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@SpringBootTest
@Import(Constant.class)
class YandexSpellerClientTest {
    @RegisterExtension
    private static final WireMockExtension yandexApiMock = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public RestClient yandexSpellerMockRestClient() {
            return RestClient.builder()
                    .baseUrl(yandexApiMock.baseUrl())
                    .build();
        }
    }

    @MockitoBean
    private YandexSpellerRequestConverter converter;

    @Autowired
    private YandexSpellerClient yandexSpellerClient;

    private static YandexSpellerRequestDto request;

    @BeforeAll
    static void beforeAll() {
        request = YandexSpellerRequestDto.builder()
                .textParts(List.of())
                .lang("en")
                .build();
    }

    @BeforeEach
    void setUp() {
        when(converter.convert(request)).thenReturn("query string");
    }

    @Test
    void checkTextsSuccessfullyTest() {
        String apiResponseJson = """
                [[
                    {
                        "pos": 3,
                        "len": 4,
                        "s": ["Test"]
                    }
                ]]
                """;

        yandexApiMock.stubFor(WireMock.post(WireMock.urlEqualTo(Constant.YANDEX_SPELLER_URI))
                .willReturn(ok()
                        .withHeader(TestConstant.CONTENT_TYPE_HEADER, MediaType.APPLICATION_JSON.toString())
                        .withBody(apiResponseJson)));

        List<List<YandexSpellerResponseDto>> apiResponse = yandexSpellerClient.checkTexts(request);

        assertThat(apiResponse).isNotNull().isNotEmpty().hasSize(1);
        assertThat(apiResponse.get(0)).isNotNull().isNotEmpty().hasSize(1);

        YandexSpellerResponseDto response = apiResponse.get(0).get(0);

        assertThat(response).isNotNull();
        assertThat(response.getPos()).isEqualTo(3);
        assertThat(response.getLen()).isEqualTo(4);
        assertThat(response.getS()).isNotNull().isNotEmpty().hasSize(1).contains("Test");
    }

    @Test
    void checkTextsWhenApiReturnsClientErrorTest() {
        String errorMessage = "Invalid request";

        yandexApiMock.stubFor(WireMock.post(WireMock.urlEqualTo(Constant.YANDEX_SPELLER_URI))
                .willReturn(badRequest().withBody(errorMessage)));

        assertThatThrownBy(() -> yandexSpellerClient.checkTexts(request))
                .isInstanceOf(YandexApiException.class)
                .hasMessageContaining("Invalid request to Yandex Speller API")
                .hasMessageContaining(errorMessage);
    }

    @Test
    void checkTextsWhenApiReturnsServerErrorTest() {
        String errorMessage = "Internal server error";

        yandexApiMock.stubFor(WireMock.post(WireMock.urlEqualTo(Constant.YANDEX_SPELLER_URI))
                .willReturn(serverError().withBody(errorMessage)));

        assertThatThrownBy(() -> yandexSpellerClient.checkTexts(request))
                .isInstanceOf(YandexApiException.class)
                .hasMessageContaining("Yandex Speller API is temporarily unavailable")
                .hasMessageContaining(errorMessage);
    }

    @Test
    void checkTextsWhenConnectionToApiFailedTest() {
        yandexApiMock.stubFor(WireMock.post(WireMock.urlEqualTo(Constant.YANDEX_SPELLER_URI))
                .willReturn(aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER)));

        assertThatThrownBy(() -> yandexSpellerClient.checkTexts(request))
                .isInstanceOf(YandexApiException.class)
                .hasMessageContaining("Failed to connect to Yandex Speller API");
    }

    @Test
    void checkTextsWhenApiReturnsUnexpectedErrorTest() {
        yandexApiMock.stubFor(WireMock.post(WireMock.urlEqualTo(Constant.YANDEX_SPELLER_URI))
                .willReturn(aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK)));

        assertThatThrownBy(() -> yandexSpellerClient.checkTexts(request))
                .isInstanceOf(YandexApiException.class)
                .hasMessageContaining("Error while calling Yandex Speller API");
    }
}