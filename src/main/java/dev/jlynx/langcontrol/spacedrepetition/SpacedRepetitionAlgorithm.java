package dev.jlynx.langcontrol.spacedrepetition;

import dev.jlynx.langcontrol.flashcard.Flashcard;

/**
 * Represents the contract for implementing spaced repetition algorithms.
 * These algorithms calculate and update review intervals for flashcards
 * based on user performance.
 *
 * @see RatingType
 * @see Flashcard
 */
public interface SpacedRepetitionAlgorithm {

    /**
     * Applies the spaced repetition algorithm to a flashcard based on the given rating.
     * This method updates the card's properties such as its interval and next review time
     * according to the selected algorithm.
     *
     * @param card   The {@link Flashcard} object to which the algorithm is applied.
     * @param rating The {@link RatingType} representing the quality of the user's review performance.
     */
    void apply(Flashcard card, RatingType rating);

    /**
     * Calculates the length of the interval in minutes that would be applied to the card if it was given the specified
     * rating. This method does not apply the algorithm to the card, so it doesn't modify the Flashcard object's
     * state - it only returns the number of minutes.
     *
     * @param card   A Flashcard object to make the calculation for
     * @param rating The rating that would be applied to the card
     * @return the number of minutes constituting the next interval or null if the wrong RatingType is applied
     */
    Long calculateNextInterval(Flashcard card, RatingType rating);
}
