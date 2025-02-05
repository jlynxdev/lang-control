package dev.jlynx.langcontrol.deck;

import dev.jlynx.langcontrol.deck.dto.CreateDeckRequest;
import dev.jlynx.langcontrol.deck.dto.DeckOverview;
import dev.jlynx.langcontrol.deck.dto.DeckDetails;
import dev.jlynx.langcontrol.deck.dto.UpdateDeckRequest;
import dev.jlynx.langcontrol.deck.view.DeckView;

import java.util.List;

/**
 * Service interface for managing deck-related operations. A deck is a collection of flashcards
 * intended for learning purposes. This interface provides methods for creating, retrieving,
 * updating and deleting decks along with extracting deck details.
 *
 * <p>Decks are managed at the user level, allowing operations to focus on user-specific collections.
 *
 * <ul>
 *   <li>{@link #createNewDeck(CreateDeckRequest)} - Creates a new deck based on input details.</li>
 *   <li>{@link #getAllCurrentUserProfileDecks()} - Retrieves all decks owned by the current user.</li>
 *   <li>{@link #getDeckById(long)} - Fetches a specific deck by its identifier.</li>
 *   <li>{@link #updateDeck(long, UpdateDeckRequest)} - Updates a specific deck with the provided details.</li>
 *   <li>{@link #deleteDeck(long)} - Deletes a specific deck by its identifier.</li>
 *   <li>{@link #extractDeckDetails(long)} - Extracts detailed information about a specific deck.</li>
 * </ul>
 */
public interface DeckService {

    DeckOverview createNewDeck(CreateDeckRequest body);

    List<DeckView> getAllCurrentUserProfileDecks();

    DeckOverview getDeckById(long deckId);

    void updateDeck(long deckId, UpdateDeckRequest body);

    void deleteDeck(long deckId);

    DeckDetails extractDeckDetails(long deckId);
}
