package by.senla.errorfreetext.util;

public final class Constant {
    public static final String ONLY_SPECIALS_PATTERN = "^[\\d\\s\\p{Punct}]+$";
    public static final String CONTAINS_DIGIT_PATTERN = ".*\\d.*";
    public static final String CONTAINS_URL_PATTERN = ".*http(s)?://\\\\w+\\\\.\\\\w{2,3}(\\\\S+)?.*";

    public static final String QUERY_PARAM_LANG = "lang";
    public static final String QUERY_PARAM_OPTIONS = "options";
    public static final String YANDEX_SPELLER_URI = "/checkTexts";

    public static final String ONLY_SPECIALS_EXC_MESSAGE = "Text cannot contain only digits and special characters";
    public static final String INVALID_LANG_EXC_MESSAGE = "Invalid language code: %s. Must be EN or RU";
    public static final String TASK_NOT_FOUND_EXC_MESSAGE = "Task with id %s not found";
    public static final String INVALID_API_REQUEST_EXC_MESSAGE = "Invalid request to Yandex Speller API: %s";
    public static final String API_IS_UNAVAILABLE_EXC_MESSAGE = "Yandex Speller API is temporarily unavailable: %s";
    public static final String FAILED_TO_CONNECT_API_EXC_MESSAGE = "Failed to connect to Yandex Speller API: %s";
    public static final String UNEXPECTED_API_ERROR_EXC_MESSAGE = "Error while calling Yandex Speller API: %s";
    public static final String VALIDATION_FAILED_EXC_MESSAGE = "Validation failed: %s";
    public static final String ERROR_MESSAGE_PART_FORMAT = "%s: %s";
    public static final String DATABASE_ERROR_EXC_MESSAGE = "Database error: %s";
    public static final String INTERNAL_SERVER_ERROR_EXC_MESSAGE = "Internal server error: %s";

    private Constant() {
    }
}