package dev.jlynx.langcontrol.spacedrepetition;

import dev.jlynx.langcontrol.flashcard.WordFlashcard;
import dev.jlynx.langcontrol.flashcard.WordFlashcardService;
import dev.jlynx.langcontrol.spacedrepetition.dto.FlashcardRatingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SpacedRepetitionServiceImpl implements SpacedRepetitionService {

    private static final Logger LOG = LoggerFactory.getLogger(SpacedRepetitionServiceImpl.class);

    private final WordFlashcardService wordFlashcardService;
    private final SpacedRepetitionAlgorithm spacedRepetitionAlgorithm;

    @Autowired
    public SpacedRepetitionServiceImpl(WordFlashcardService wordFlashcardService,
                                       SpacedRepetitionAlgorithm spacedRepetitionAlgorithm) {
        this.wordFlashcardService = wordFlashcardService;
        this.spacedRepetitionAlgorithm = spacedRepetitionAlgorithm;
    }

    /**
     * Applies a rating to a flashcard, updates its state in the spaced repetition system
     * and returns a response with the updated information.
     *
     * @param flashcardId the ID of the flashcard to which the rating is being applied; must not be {@code null}.
     * @param ratingType  the {@link RatingType} representing the user's evaluation of the flashcard;
     *                    must not be {@code null}.
     * @return a {@link FlashcardRatingResponse} containing the flashcard ID and flags indicating whether
     * it switched to review mode or learn mode.
     * @throws IllegalArgumentException if {@code flashcardId} or {@code ratingType} is {@code null}.
     */
    @Transactional
    @Override
    public FlashcardRatingResponse applyRating(Long flashcardId, RatingType ratingType) {
        if (flashcardId == null || ratingType == null) {
            throw new IllegalArgumentException("Arguments cannot be null.");
        }
        WordFlashcard flashcard = wordFlashcardService.getCardById(flashcardId);
        boolean inLearnModeBefore = flashcard.isInLearnMode();

        spacedRepetitionAlgorithm.apply(flashcard, ratingType);
        LOG.debug("Rating applied. Next view datetime in UTC is: {}", flashcard.getNextView());

        boolean inLearnModeAfter = flashcard.isInLearnMode();
        boolean switchedToReviewMode = inLearnModeBefore && !inLearnModeAfter;
        boolean switchedToLearnMode = !inLearnModeBefore && inLearnModeAfter;

        return new FlashcardRatingResponse(flashcard.getId(),
                switchedToReviewMode, switchedToLearnMode);
    }
}
