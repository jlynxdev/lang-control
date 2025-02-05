package dev.jlynx.langcontrol.deck.dto;

/**
 * Represents the detailed view of a deck.
 * A deck is a collection of flashcards intended for learning purposes.
 * This class provides specific details about the deck.
 *
 * <ul>
 *   <li>{@code id} - The unique identifier of the deck.</li>
 *   <li>{@code name} - The name of the deck.</li>
 *   <li>{@code totalCards} - The total number of flashcards in the deck.</li>
 *   <li>{@code readyForReview} - The number of flashcards eligible for review.</li>
 * </ul>
 *
 * @see dev.jlynx.langcontrol.deck.Deck
 * @see DeckOverview
 * @see CreateDeckRequest
 * @see UpdateDeckRequest
 * @see dev.jlynx.langcontrol.flashcard.Flashcard
 */
public record DeckDetails(long id, String name, int totalCards, int readyForReview) {
}
