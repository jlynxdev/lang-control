package dev.jlynx.langcontrol.spacedrepetition;

import com.fasterxml.jackson.annotation.JsonValue;
import dev.jlynx.langcontrol.flashcard.Flashcard;

/**
 * Represents the different types of ratings that users can apply to flashcards in learning
 * and review sessions. Each {@link RatingType} is tailored for use in the spaced repetition
 * algorithm.
 *
 * <p>Rating types are used to classify the user's level of knowledge or recall
 * for a specific flashcard, affecting future review intervals in the algorithm.
 *
 * @see SpacedRepetitionAlgorithm
 * @see BasicSpacedRepetitionAlgorithm
 */
public enum RatingType {

    /**
     * Represents the rating type that indicates a user knows the material during a learning session.
     * <p>
     * It is primarily used in spaced repetition algorithms and flashcard learning systems.
     * <p>Only compatible with flashcards in Learn Mode.</p>
     *
     * @see RatingType
     * @see SpacedRepetitionService#applyRating(Long, RatingType)
     * @see SpacedRepetitionAlgorithm#apply(Flashcard, RatingType)
     */
    LEARN_KNOW("know"),

    /**
     * Represents the state where the user has indicated they "don't know" the answer during a learning session.
     * <p>
     * This {@link RatingType} is used to track flashcards requiring more focus.
     * <p>Only compatible with flashcards in Learn Mode.</p>
     *
     * @see RatingType
     * @see SpacedRepetitionService#applyRating(Long, RatingType)
     * @see SpacedRepetitionAlgorithm#apply(Flashcard, RatingType)
     */
    LEARN_DONT_KNOW("dknow"),

    /**
     * Represents the "remember" rating type in the context of spaced repetition.
     * <p>
     * This rating indicates that the user successfully recalled the information
     * associated with the specific flashcard or step, reinforcing its retention.
     * <p>Only compatible with flashcards in Review Mode.</p>
     *
     * @see RatingType
     * @see SpacedRepetitionService#applyRating(Long, RatingType)
     * @see SpacedRepetitionAlgorithm#apply(Flashcard, RatingType)
     */
    REVIEW_REMEMBER("remember"),

    /**
     * Represents a rating type used in spaced repetition to indicate that a flashcard is only partially remembered.
     * This rating is associated with specific adjustment logic in algorithms such as {@link SpacedRepetitionAlgorithm}.
     * <p>Only compatible with flashcards in Review Mode.</p>
     *
     * @see RatingType
     * @see SpacedRepetitionService#applyRating(Long, RatingType)
     * @see SpacedRepetitionAlgorithm#apply(Flashcard, RatingType)
     */
    REVIEW_PARTIALLY("partially"),

    /**
     * Represents the "forgot" rating in the {@link RatingType} enumeration.
     * <p>
     * This rating indicates that the user has forgotten the information associated with the reviewed flashcard.
     * It is typically used in spaced repetition algorithms to adjust the flashcard's review interval accordingly.
     * <p>Only compatible with flashcards in Review Mode.</p>
     *
     * @see RatingType
     * @see SpacedRepetitionService#applyRating(Long, RatingType)
     * @see SpacedRepetitionAlgorithm#apply(Flashcard, RatingType)
     */
    REVIEW_FORGOT("forgot");

    private String urlValue;

    RatingType(String urlValue) {
        this.urlValue = urlValue;
    }

    /**
     * Retrieves the URL-compatible string value associated with this {@link RatingType}.
     * The returned string represents the unique identifier used for JSON serialisation
     * or compatibility with URL params.
     *
     * @return a {@link String} representing the URL value of this {@link RatingType}.
     */
    @JsonValue
    public String getUrlValue() {
        return urlValue;
    }
}
