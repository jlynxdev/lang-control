package dev.jlynx.langcontrol.deck.dto;

import dev.jlynx.langcontrol.lang.LanguageCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Represents a request to create a new deck. A deck is defined by its name, a target language
 * and a source language. Validation constraints ensure proper input values.
 *
 * <ul>
 *   <li>{@code name} - The name of the deck; must not be blank and has a maximum length of 30 characters.</li>
 *   <li>{@code targetLang} - The language code representing the target language for the deck; must not be null.</li>
 *   <li>{@code sourceLang} - The language code representing the source language for the deck; must not be null.</li>
 * </ul>
 *
 * @see LanguageCode
 */
public record CreateDeckRequest(
        @NotBlank
        @Size(max = 30)
        String name,

        @NotNull
        LanguageCode targetLang,

        @NotNull
        LanguageCode sourceLang
) {}
