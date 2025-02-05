package dev.jlynx.langcontrol.flashcard;

import dev.jlynx.langcontrol.flashcard.dto.*;
import dev.jlynx.langcontrol.url.SortOrder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Validated
@RequestMapping("${apiPref}/cards")
@RestController
public class WordFlashcardController {

    private final WordFlashcardService wordFlashcardService;

    @Autowired
    public WordFlashcardController(WordFlashcardService wordFlashcardService) {
        this.wordFlashcardService = wordFlashcardService;
    }

    /**
     * Creates a new flashcard based on the provided request body.
     * <p>
     * This method leverages {@link WordFlashcardService#createNewFlashcard} to handle
     * the creation process and returns the created flashcard's overview information
     * wrapped in a {@link ResponseEntity}.
     *
     * @param body the request containing details for creating the new flashcard,
     *             validated against the constraints defined in {@link CreateWordFlashcardRequest}.
     * @return a {@link ResponseEntity} containing the created flashcard's overview
     *         as a {@link WordFlashcardOverviewResponse} and an HTTP status of {@code 201 (Created)}.
     */
    @PostMapping
    public ResponseEntity<WordFlashcardOverviewResponse> createFlashcard(@RequestBody @Valid CreateWordFlashcardRequest body) {
        var cardOverview = wordFlashcardService.createNewFlashcard(body);
        return new ResponseEntity<>(cardOverview, HttpStatus.CREATED);
    }

    /**
     * Creates a new flashcard in Zen mode.
     * <p>
     * This method creates a flashcard based on the information provided in the
     * {@link CreateWordFlashcardZenModeRequest} request body and returns an overview of the created flashcard.
     *
     * @param body the {@link CreateWordFlashcardZenModeRequest} containing data for the new flashcard
     * @return a {@link ResponseEntity} containing the {@link WordFlashcardOverviewResponse} of the created flashcard with HTTP status {@code CREATED}
     */
    @PostMapping("/zen-mode")
    public ResponseEntity<WordFlashcardOverviewResponse> createFlashcardInZenMode(@RequestBody @Valid CreateWordFlashcardZenModeRequest body) {
        var cardOverview = wordFlashcardService.createNewFlashcardZenMode(body);
        return new ResponseEntity<>(cardOverview, HttpStatus.CREATED);
    }

    /**
     * Retrieves a paginated list of flashcards with optional filtering, sorting and ordering.
     *
     * @param deckId the ID of the deck to filter flashcards by, optional. Must be a positive number if provided.
     * @param pageNum the page number to retrieve, optional. Default is 0 and must be positive or zero.
     * @param pageSize the number of flashcards per page, optional. Default is 10 and must be between 5 and 100.
     * @param sortBy the attribute by which to sort the flashcards, optional. Default is {@code created}.
     * @param order the sorting order (ascending or descending), optional. Default is {@code asc}.
     * @return a {@link ResponseEntity} containing a {@link Page} of {@link WordFlashcardView} objects.
     */
    @GetMapping
    public ResponseEntity<Page<WordFlashcardView>> getCardsPaginated(
            @RequestParam(name = "deck", required = false) @Positive Long deckId,
            @RequestParam(name = "page", required = false, defaultValue = "0") @PositiveOrZero int pageNum,
            @RequestParam(name = "size", required = false, defaultValue = "10") @Min(5) @Max(100) int pageSize,
            @RequestParam(name = "sort", required = false, defaultValue = "created") FlashcardSortBy sortBy,
            @RequestParam(name = "order", required = false, defaultValue = "asc") SortOrder order
            ) {
        var cards = wordFlashcardService.getFlashcards(Optional.ofNullable(deckId), pageNum, pageSize, sortBy, order);
        return ResponseEntity.ok(cards);
    }

    /**
     * Retrieves a word flashcard overview by its unique identifier.
     *
     * @param cardId The unique identifier of the flashcard. Must be a positive number greater than or equal to 1.
     * @return A {@link ResponseEntity} containing a {@link WordFlashcardOverviewResponse}
     *         object with the flashcard's details.
     */
    @GetMapping("/{id}")
    public ResponseEntity<WordFlashcardOverviewResponse> getCardById(@Min(1) @PathVariable("id") long cardId) {
        var cardOverview = wordFlashcardService.getCardOverviewById(cardId);
        return ResponseEntity.ok(cardOverview);
    }

    /**
     * Updates an existing flashcard with new data.
     *
     * @param cardId the identifier of the flashcard to update; must be a positive number
     * @param body the request body containing updated flashcard data; validated using {@link UpdateWordFlashcardRequest}
     * @return {@code ResponseEntity<Void>} with {@code HttpStatus.NO_CONTENT} on successful update
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateFlashcard(
            @Min(1) @PathVariable("id") long cardId,
            @RequestBody @Valid UpdateWordFlashcardRequest body
    ) {
        wordFlashcardService.updateFlashcard(cardId, body);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Deletes a flashcard with the specified ID.
     *
     * @param cardId The ID of the flashcard to be deleted. Must be a positive number.
     * @return A {@link ResponseEntity} with an HTTP status of {@code NO_CONTENT}
     *         indicating successful deletion of the flashcard.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlashcard(@Min(1) @PathVariable("id") long cardId) {
        wordFlashcardService.deleteFlashcard(cardId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Retrieves interval forecast data for a flashcard identified by {@code cardId}.
     *
     * @param cardId the ID of the flashcard for which the interval forecasts are requested; must be a positive value.
     * @return a {@link ResponseEntity} object containing {@link IntervalForecastResponse} with forecast details.
     */
    @GetMapping("/{id}/forecasts")
    ResponseEntity<IntervalForecastResponse> getIntervalForecasts(@Min(1) @PathVariable("id") long cardId) {
        IntervalForecastResponse forecasts = wordFlashcardService.getIntervalForecasts(cardId);
        return ResponseEntity.ok(forecasts);
    }
}
