package by.senla.errorfreetext.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class RestClientConfig {
    @Value("${yandex.api.url}")
    private String url;

    @Value("${yandex.api.connect-timeout}")
    private int connectTimeout;

    @Value("${yandex.api.read-timeout}")
    private int readTimeout;

    @Bean
    public RestClient yandexSpellerRestClient(RestClient.Builder restClientBuilder) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(connectTimeout))
                .build();

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofMillis(readTimeout));

        return restClientBuilder
                .baseUrl(url)
                .requestFactory(requestFactory)
                .build();
    }
}