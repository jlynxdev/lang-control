package dev.jlynx.langcontrol.deck.view;

import dev.jlynx.langcontrol.lang.LanguageCode;

/**
 * Represents a view of a deck entity with its identifying details
 * and associated language information.
 *
 * <ul>
 *   <li>{@code id} - Unique identifier of the deck.</li>
 *   <li>{@code name} - Name of the deck.</li>
 *   <li>{@code targetLang} - The target {@link LanguageCode} for the deck.</li>
 *   <li>{@code sourceLang} - The source {@link LanguageCode} for the deck.</li>
 * </ul>
 */
public record DeckView(Long id, String name, LanguageCode targetLang, LanguageCode sourceLang) {
}
