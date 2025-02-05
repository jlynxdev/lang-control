package dev.jlynx.langcontrol.spacedrepetition.dto;

import dev.jlynx.langcontrol.spacedrepetition.RatingType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Represents a request to rate a flashcard during a learning or review session.
 *
 * <p>The {@code FlashcardRatingRequest} contains the ID of the flashcard being rated
 * and its {@link RatingType}, which determines the user's evaluation of their knowledge
 * or recall for the card.
 *
 * @param cardId the ID of the flashcard being rated, must be greater than or equal to 1.
 * @param rating the {@link RatingType} applied to the flashcard, cannot be {@code null}.
 * @see RatingType
 * @see FlashcardRatingResponse
 */
public record FlashcardRatingRequest(
        @Min(1)
        long cardId,
        @NotNull
        RatingType rating
) {
}
