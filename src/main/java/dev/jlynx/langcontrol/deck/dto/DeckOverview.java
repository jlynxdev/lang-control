package dev.jlynx.langcontrol.deck.dto;

import dev.jlynx.langcontrol.deck.Deck;
import dev.jlynx.langcontrol.lang.LanguageCode;

/**
 * Represents a summarised view of a deck. A deck is a collection of flashcards designed for learning purposes.
 *
 * <ul>
 *   <li>{@code id} - The unique identifier of the deck.</li>
 *   <li>{@code name} - The name of the deck.</li>
 *   <li>{@code targetLang} - The target language of the deck represented by {@link LanguageCode}.</li>
 *   <li>{@code sourceLang} - The source language of the deck represented by {@link LanguageCode}.</li>
 * </ul>
 *
 * <p>Primarily used for providing an overview of a deck's basic attributes
 * without detailing its flashcards or other specific information.
 *
 * @see Deck
 * @see LanguageCode
 * @see DeckDetails
 */
public record DeckOverview(long id, String name, LanguageCode targetLang, LanguageCode sourceLang) {

    /**
     * Converts a {@link Deck} entity into a {@link DeckOverview} object.
     *
     * @param entity the {@link Deck} entity to be converted. Must not be {@code null}.
     * @return a {@link DeckOverview} instance representing the provided entity's attributes.
     */
    public static DeckOverview fromEntity(Deck entity) {
        return new DeckOverview(
                entity.getId(),
                entity.getName(),
                entity.getTargetLang(),
                entity.getSourceLang()
        );
    }
}
