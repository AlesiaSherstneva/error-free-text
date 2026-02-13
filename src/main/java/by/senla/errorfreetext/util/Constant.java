package by.senla.errorfreetext.util;

public final class Constant {
    public static final String ONLY_SPECIALS_PATTERN = "^[\\d\\s\\p{Punct}]+$";

    public static final String ONLY_SPECIALS_EXC_MESSAGE = "Text cannot contain only digits and special characters";
    public static final String INVALID_LANG_EXC_MESSAGE = "Invalid language code: %s. Must be EN or RU";

    private Constant() {
    }
}