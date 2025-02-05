package dev.jlynx.langcontrol.deck.dto;

import dev.jlynx.langcontrol.deck.DeckController;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Represents a request to update an existing deck. A deck's name can be updated
 * while adhering to validation rules.
 *
 * <ul>
 *   <li>{@code name} - The new name of the deck; must not be blank and has a maximum length of 30 characters.</li>
 * </ul>
 *
 * <p>Primarily used in the context of a REST API for updating decks via {@link DeckController}.
 *
 * @see CreateDeckRequest
 * @see DeckDetails
 * @see DeckOverview
 */
public record UpdateDeckRequest(@NotBlank @Size(max = 30) String name) {
}
