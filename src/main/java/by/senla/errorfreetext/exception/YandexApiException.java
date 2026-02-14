package by.senla.errorfreetext.exception;

/**
 * Exception thrown when an error occurs while communicating with Yandex Speller API.
 * Wraps various API-related errors like connection issues, timeouts, or invalid responses.
 */
public class YandexApiException extends RuntimeException {
    public YandexApiException(String message) {
        super(message);
    }
}