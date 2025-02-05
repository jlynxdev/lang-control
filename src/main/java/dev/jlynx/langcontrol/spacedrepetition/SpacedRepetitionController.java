package dev.jlynx.langcontrol.spacedrepetition;

import dev.jlynx.langcontrol.flashcard.WordFlashcardService;
import dev.jlynx.langcontrol.flashcard.dto.WordFlashcardView;
import dev.jlynx.langcontrol.spacedrepetition.dto.FlashcardRatingRequest;
import dev.jlynx.langcontrol.spacedrepetition.dto.FlashcardRatingResponse;
import dev.jlynx.langcontrol.url.SortOrder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing spaced repetition operations. Provides endpoints for
 * fetching ready-to-review flashcards and passing flashcard ratings to the
 * spaced repetition algorithm.
 * <p>
 * This controller handles interactions with {@link WordFlashcardService} and {@link SpacedRepetitionService}.
 */
@Validated
@RequestMapping("${apiPref}/sr")
@RestController
public class SpacedRepetitionController {

    private final WordFlashcardService wordFlashcardService;
    private final SpacedRepetitionService spacedRepetitionService;

    @Autowired
    public SpacedRepetitionController(
            WordFlashcardService wordFlashcardService,
            SpacedRepetitionService spacedRepetitionService
    ) {
        this.wordFlashcardService = wordFlashcardService;
        this.spacedRepetitionService = spacedRepetitionService;
    }

    /**
     * Retrieves a list of ready-to-review flashcards for a specified deck, sorted and limited
     * based on input parameters.
     *
     * @param deckId the unique identifier of the deck; must be greater than or equal to 1.
     * @param limit  the maximum number of cards to return; must be at least 3. The default value is 10.
     * @param sortBy the sorting field for the cards, defaulting to {@code SpacedRepetitionSortBy.VIEW}.
     *               Uses {@link SpacedRepetitionSortBy}.
     * @param order  the sorting order, either ascending or descending, defaulting to {@code SortOrder.ASC}.
     *               Uses {@link SortOrder}.
     * @return a {@link ResponseEntity} containing a list of {@link WordFlashcardView}, representing the cards
     * ready for reviewing.
     */
    @GetMapping
    public ResponseEntity<?> getReadyForViewCardsByDeck(
            @Min(1) @RequestParam("deck") long deckId,
            @Min(3) @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
            @RequestParam(value = "sort", required = false, defaultValue = "view") SpacedRepetitionSortBy sortBy,
            @RequestParam(value = "order", required = false, defaultValue = "asc") SortOrder order
    ) {
        List<WordFlashcardView> readyCards = wordFlashcardService.fetchReadyForView(deckId, limit, sortBy, order);
        return ResponseEntity.ok(readyCards);
    }

    /**
     * Handles the rating of a flashcard by applying the specified rating through the spaced repetition service.
     *
     * @param body the {@link FlashcardRatingRequest} containing the flashcard ID and the rating type to be applied.
     * @return a {@link ResponseEntity} containing a {@link FlashcardRatingResponse} that indicates the updated
     * status of the flashcard, such as whether it switched to review mode or learn mode.
     */
    @PostMapping("/rating")
    public ResponseEntity<FlashcardRatingResponse> handleFlashcardRating(@Valid @RequestBody FlashcardRatingRequest body) {
        FlashcardRatingResponse flashcardRatedOverview = spacedRepetitionService.applyRating(
                body.cardId(),
                body.rating()
        );
        return ResponseEntity.ok(flashcardRatedOverview);
    }
}
