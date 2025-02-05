package dev.jlynx.langcontrol.lang.dto;

import dev.jlynx.langcontrol.lang.LanguageCode;

/**
 * Represents an overview of a language and its corresponding ISO 639-1 code.
 * Used specifically for serialising the {@link LanguageCode} information into JSON format.
 *
 * <p>This record provides a concise representation of a language by
 * combining its full name and its standardised ISO 639-1 code.
 *
 * @param language the name of the language in English
 * @param code     the ISO 639-1 language code
 */
public record LanguageCodeOverview(String language, String code) {

    /**
     * Creates a new {@link LanguageCodeOverview} instance from the given {@link LanguageCode}.
     *
     * @param langCode the {@link LanguageCode} containing the language's name and ISO 639-1 code
     * @return a {@link LanguageCodeOverview} representing the given {@link LanguageCode}
     */
    public static LanguageCodeOverview fromLanguageCode(LanguageCode langCode) {
        return new LanguageCodeOverview(langCode.getFullLanguageName(), langCode.getCode());
    }
}
