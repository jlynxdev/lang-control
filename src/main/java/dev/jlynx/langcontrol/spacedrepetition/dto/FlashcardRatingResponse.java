package dev.jlynx.langcontrol.spacedrepetition.dto;

/**
 * Represents the response after applying a rating to a flashcard.
 * The response indicates the ID of the flashcard that was rated
 * and whether it switched to review mode or learn mode as a result of the rating.
 *
 * @param id                   the unique identifier of the flashcard being rated
 * @param switchedToReviewMode whether the flashcard was moved to review mode
 * @param switchedToLearnMode  whether the flashcard was moved to learn mode
 * @see FlashcardRatingRequest
 */
public record FlashcardRatingResponse(long id, boolean switchedToReviewMode, boolean switchedToLearnMode) {
}
