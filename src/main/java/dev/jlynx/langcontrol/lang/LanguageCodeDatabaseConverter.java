package dev.jlynx.langcontrol.lang;

import dev.jlynx.langcontrol.exception.InvalidDatabaseValueException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Provides the necessary logic for conversions between the {@link LanguageCode} enum and a database
 * column of type {@code varchar}.
 */
@Converter(autoApply = true)
public class LanguageCodeDatabaseConverter implements AttributeConverter<LanguageCode, String> {

    /**
     * Converts a {@link LanguageCode} enum instance to its corresponding database column representation
     * as a {@code String}.
     *
     * @param languageCode the {@link LanguageCode} enum to be converted; may not be {@code null}.
     * @return the ISO 639-1 language code as a {@code String}.
     */
    @Override
    public String convertToDatabaseColumn(LanguageCode languageCode) {
        return languageCode.getCode();
    }

    /**
     * Converts a database column value to its corresponding {@link LanguageCode}
     * enumeration value.
     *
     * @param code the ISO 639-1 language code from the database.
     * @return the matching {@link LanguageCode} enumeration value.
     * @throws InvalidDatabaseValueException if the given {@code code} does not
     *                                       correspond to any {@link LanguageCode}.
     */
    @Override
    public LanguageCode convertToEntityAttribute(String code) {
        return LanguageCode.findByCode(code)
                .orElseThrow(() ->
                        new InvalidDatabaseValueException("Could not find a language code \"%s\"".formatted(code))
                );
    }
}
