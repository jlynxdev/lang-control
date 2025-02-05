package dev.jlynx.langcontrol.deck;

import dev.jlynx.langcontrol.deck.dto.CreateDeckRequest;
import dev.jlynx.langcontrol.deck.dto.DeckOverview;
import dev.jlynx.langcontrol.deck.dto.DeckDetails;
import dev.jlynx.langcontrol.deck.dto.UpdateDeckRequest;
import dev.jlynx.langcontrol.deck.view.DeckView;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Handles REST API operations for managing decks.
 * A deck is a collection of flashcards associated with learning.
 * <p>The controller supports the following operations:
 * <ul>
 *   <li>Create a new deck.</li>
 *   <li>Retrieve all decks of the current user.</li>
 *   <li>Retrieve a specific deck by its unique identifier ({@code id}).</li>
 *   <li>Retrieve detailed information about a specific deck.</li>
 *   <li>Update a deck's details.</li>
 *   <li>Delete a specific deck.</li>
 * </ul>
 *
 * <p>All API endpoints require the deck's identifier or relevant request payloads.
 * Validation annotations ensure input correctness.
 *
 * <ul>
 *   <li>{@link DeckService} is the main dependency providing business logic for deck operations.</li>
 *   <li>Uses {@link ResponseEntity} to encapsulate HTTP responses.</li>
 * </ul>
 *
 * <p>Mappings:
 * <ul>
 *   <li>{@code POST /api/decks} - Create a new deck.</li>
 *   <li>{@code GET /api/decks} - Fetch all user decks.</li>
 *   <li>{@code GET /api/decks/{id}} - Fetch details of a deck by ID.</li>
 *   <li>{@code GET /api/decks/{id}/details} - Fetch specific details of a deck.</li>
 *   <li>{@code PUT /api/decks/{id}} - Update a deck.</li>
 *   <li>{@code DELETE /api/decks/{id}} - Delete a deck.</li>
 * </ul>
 *
 * @see DeckService
 */
@Validated
@RestController
@RequestMapping("${apiPref}/decks")
public class DeckController {

    private final DeckService deckService;

    @Autowired
    public DeckController(DeckService deckService) {
        this.deckService = deckService;
    }

    /**
     * Creates a new deck based on the provided request data.
     *
     * @param body the request payload containing the details for the new deck;
     *             must be valid according to the constraints defined in {@link CreateDeckRequest}.
     * @return a {@link ResponseEntity} containing the created deck's overview as
     *         {@link DeckOverview} and an HTTP status of {@code 201 CREATED}.
     */
    @PostMapping
    public ResponseEntity<DeckOverview> createDeck(@Valid @RequestBody CreateDeckRequest body) {
        DeckOverview response = deckService.createNewDeck(body);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Retrieves all decks associated with the currently authenticated user.
     * <p>The method fetches a list of {@link DeckView} objects which represent
     * the user's decks in a summarised view format.
     *
     * @return a {@link ResponseEntity} containing a list of {@link DeckView} objects,
     *         wrapped with an HTTP status of {@code 200 OK}.
     */
    @GetMapping
    public ResponseEntity<List<DeckView>> getAllUserDecks() {
        List<DeckView> decks = deckService.getAllCurrentUserProfileDecks();
        return ResponseEntity.ok(decks);
    }

    /**
     * Retrieves an overview of a deck based on its unique identifier.
     *
     * @param deckId the unique identifier of the requested deck; must be greater than or equal to 1.
     * @return a {@link ResponseEntity} containing the {@link DeckOverview} if the deck exists.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DeckOverview> getDeck(@Min(1) @PathVariable("id") long deckId) {
        DeckOverview deckOverview = deckService.getDeckById(deckId);
        return ResponseEntity.ok(deckOverview);
    }

    /**
     * Retrieves detailed information for a specific deck based on its unique identifier.
     *
     * @param deckId the unique identifier of the deck to retrieve; must be a positive number.
     * @return a {@link ResponseEntity} containing the {@link DeckDetails} object with the deck's detailed information.
     */
    @GetMapping("/{id}/details")
    public ResponseEntity<DeckDetails> getDeckDetails(@Min(1) @PathVariable("id") long deckId) {
        DeckDetails deckDetails = deckService.extractDeckDetails(deckId);
        return ResponseEntity.ok(deckDetails);
    }

    /**
     * Updates the details of an existing deck.
     * <p>The deck is identified by its unique {@code id}
     * and the new details are provided in the request body.</p>
     *
     * @param deckId the unique identifier of the deck to be updated; must be {@code >= 1}.
     * @param body the {@link UpdateDeckRequest} containing the updated deck details; must be valid.
     * @return a {@link ResponseEntity} with no content and {@code HTTP 204 NO CONTENT} status.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateDeck(
            @Min(1) @PathVariable("id") long deckId,
            @RequestBody @Valid UpdateDeckRequest body
    ) {
        deckService.updateDeck(deckId, body);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Deletes a deck identified by its unique ID.
     * <p>
     * This method removes the specified deck and responds with {@link HttpStatus#NO_CONTENT}
     * when the operation is successful.
     *
     * @param deckId the unique identifier of the deck to be deleted. Must be greater than or equal to 1.
     * @return a {@link ResponseEntity} without body and a status of {@link HttpStatus#NO_CONTENT}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeck(@Min(1) @PathVariable("id") long deckId) {
        deckService.deleteDeck(deckId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
