package dev.jlynx.langcontrol.spacedrepetition;

import dev.jlynx.langcontrol.spacedrepetition.dto.FlashcardRatingResponse;

/**
 * Service interface for applying spaced repetition logic to flashcards based on user ratings.
 * This interface defines operations that integrate flashcard ratings with a spaced repetition algorithm,
 * influencing flashcard review interval scheduling.
 */
public interface SpacedRepetitionService {

    FlashcardRatingResponse applyRating(Long flashcardId, RatingType ratingType);
}
