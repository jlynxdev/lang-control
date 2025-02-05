package dev.jlynx.langcontrol.spacedrepetition;

import dev.jlynx.langcontrol.flashcard.Flashcard;
import dev.jlynx.langcontrol.util.DateTimeTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Implementation of the {@link SpacedRepetitionAlgorithm} interface
 * that uses a basic spaced repetition approach to adjust the review intervals
 * of flashcards based on user feedback.
 * <p>
 * Its main function is to update the next interval of a {@link Flashcard} as well as its
 * {@code ifactor} (interval increase factor) and {@code rFactor} (interval decrease/reduction factor).
 * <p>
 * This algorithm supports and manages two states for flashcards:
 * <ul>
 *     <li>Learn Mode: A mode where flashcards are actively being learned; consists of several predefined
 *     {@link LearnModeStep}.</li>
 *     <li>Review Mode: A mode where flashcards are being reviewed based on previous learning intervals.</li>
 * </ul>
 * The transition between modes and the calculation of the next review interval depend on
 * the user's rating ({@link RatingType}) of the flashcard.
 *
 * @see Flashcard
 */
@Component
public class BasicSpacedRepetitionAlgorithm implements SpacedRepetitionAlgorithm {

    /**
     * Represents the increment used to update the factors in the spaced repetition algorithm.
     * <p>
     * This constant is used to adjust the {@link Flashcard}'s {@code iFactor} and {@code rFactor} values
     * during the Review Mode in.
     */
    protected static final float FACTOR_UPDATE_UNIT = 0.04F;

    /**
     * The upper limit for the {@code iFactor} in the spaced repetition algorithm.
     * <p>
     * This constant is used in the implementation of the {@link BasicSpacedRepetitionAlgorithm}
     * to cap the maximum value of the {@code iFactor}, which influences the learning progression
     * of flashcards within the algorithm.
     *
     * @see BasicSpacedRepetitionAlgorithm#increaseIFactorByOneUnit(Flashcard)
     * @see BasicSpacedRepetitionAlgorithm#decreaseIFactorByOneUnit(Flashcard)
     */
    protected static final float I_FACTOR_UPPER_LIMIT = 1.4F;

    /**
     * The minimum allowable value for the {@code iFactor} in the spaced repetition algorithm,
     * used to adjust the interval factor when calculating the next review interval for a flashcard.
     * This ensures that the {@code iFactor} does not decrease below this threshold.
     *
     * @see BasicSpacedRepetitionAlgorithm#increaseIFactorByOneUnit(Flashcard)
     * @see BasicSpacedRepetitionAlgorithm#decreaseIFactorByOneUnit(Flashcard)
     */
    protected static final float I_FACTOR_BOTTOM_LIMIT = 1.1F;

    /**
     * The upper limit for the R-Factor value in the spaced repetition algorithm.
     * <p>
     * This constant restricts the maximum value that the {@code rFactor} can reach during its
     * adjustments.
     *
     * @see BasicSpacedRepetitionAlgorithm#increaseRFactorByOneUnit(Flashcard)
     * @see BasicSpacedRepetitionAlgorithm#decreaseRFactorByOneUnit(Flashcard)
     */
    protected static final float R_FACTOR_UPPER_LIMIT = 0.1F;

    /**
     * Defines the lower limit for the {@code rFactor} in the spaced repetition algorithm to ensure
     * the interval is not reduced too abruptly.
     *
     * @see BasicSpacedRepetitionAlgorithm#increaseRFactorByOneUnit(Flashcard)
     * @see BasicSpacedRepetitionAlgorithm#decreaseRFactorByOneUnit(Flashcard)
     */
    protected static final float R_FACTOR_BOTTOM_LIMIT = 0.01F;

    /**
     * The initial value for the {@code iFactor} (interval increase factor) when a new
     * instance of {@link Flashcard} is created.
     * <p>
     * This value represents the initial amount by which the {@link Flashcard} intervals are increased
     * when the user submits a {@link RatingType#REVIEW_REMEMBER} rating.
     *
     * @see RatingType
     */
    public static final float INITIAL_IFACTOR = 1.3f;

    /**
     * The initial value for the {@code rFactor} (interval reduce factor) when a new
     * instance of {@link Flashcard} is created.
     * <p>
     * This value represents the initial amount by which the {@link Flashcard} intervals are decreased
     * when the user submits a {@link RatingType#REVIEW_FORGOT} rating.
     *
     * @see RatingType
     */
    public static final float INITIAL_RFACTOR = 0.05f;

    /**
     * Represents the initial {@link LearnModeStep} for new {@link Flashcard} instances.
     * <p>
     * The value is set to {@link LearnModeStep#TWO}, which defines the second step
     * of the learning mode.
     *
     * @see LearnModeStep
     */
    public static final LearnModeStep INIT_LMS = LearnModeStep.TWO;

    /**
     * Represents the default interval in minutes for the first review of a flashcard
     * in the Review Mode. This interval is calculated as 24 hours
     * converted to minutes.
     * <p>
     * Used within the {@link BasicSpacedRepetitionAlgorithm} implementation as
     * the initial value for determining subsequent review intervals.
     */
    public static final long INITIAL_REVIEW_INTERVAL = Duration.ofHours(24).toMinutes();

    /**
     * An instance of {@link DateTimeTools} utilised for handling date-time operations
     * in the context of the spaced repetition algorithm's functionality.
     */
    private final DateTimeTools dtt;

    @Autowired
    public BasicSpacedRepetitionAlgorithm(DateTimeTools dtt) {
        this.dtt = dtt;
    }

    /**
     * Applies the spaced repetition algorithm's logic to a given flashcard
     * based on the specified rating type. This method handles updates for both
     * Learn and Review Modes according to the {@link RatingType} provided.
     * <p>
     * The method delegates to either {@link #applyLearnRating} or {@link #applyReviewRating}
     * depending on the {@link RatingType} provided.
     *
     * @param card   The {@link Flashcard} to which the algorithm is applied.
     * @param rating The {@link RatingType} used to determine the update logic.
     * @throws IllegalArgumentException if any argument is {@code null}
     * @see #applyLearnRating
     * @see #applyReviewRating
     */
    @Override
    public void apply(Flashcard card, RatingType rating) {
        if (card == null || rating == null) {
            throw new IllegalArgumentException("The arguments cannot be null when the algorithm is applied.");
        }
        if (rating.name().startsWith("LEARN")) {
            applyLearnRating(card, rating);
        } else if (rating.name().startsWith("REVIEW")) {
            applyReviewRating(card, rating);
        }
    }

    /**
     * Calculates the next interval for a given flashcard based on its current state
     * and the provided rating without changing the flashcard's state.
     * <p>
     * If the flashcard is in Learn Mode, it adjusts the interval according to the
     * current step in the learning process and the rating. If the flashcard is in Review Mode,
     * it applies an interval adjustment factor based on the rating. This method does not
     * modify the flashcard's interval, it only performs the calculation based on the
     * current flashcard state.
     *
     * @param card   The {@link Flashcard} for which the next interval is calculated.
     *               The state of the card determines whether it operates in Learn or Review Mode.
     * @param rating The {@link RatingType} that defines the user's response to the flashcard,
     *               influencing the logic used to calculate the interval.
     * @return The calculated next interval in minutes, or {@code null} if the card
     * state or rating does not align with valid transitions.
     */
    @Override
    public Long calculateNextInterval(Flashcard card, RatingType rating) {
        if (card.isInLearnMode()) {
            switch (card.getLearnModeStep()) {
                case ONE -> {
                    if (rating == RatingType.LEARN_KNOW) {
                        return Duration
                                .of(LearnModeStep.TWO.getAmountToAdd(), LearnModeStep.TWO.getTemporalUnit())
                                .toMinutes();
                    }
                    if (rating == RatingType.LEARN_DONT_KNOW) {
                        return Duration
                                .of(LearnModeStep.ONE.getAmountToAdd(), LearnModeStep.ONE.getTemporalUnit())
                                .toMinutes();
                    }
                }
                case TWO -> {
                    if (rating == RatingType.LEARN_KNOW) {
                        return INITIAL_REVIEW_INTERVAL;
                    }
                    if (rating == RatingType.LEARN_DONT_KNOW) {
                        return Duration
                                .of(LearnModeStep.ONE.getAmountToAdd(), LearnModeStep.ONE.getTemporalUnit())
                                .toMinutes();
                    }
                }
            }
        } else {
            if (rating == RatingType.REVIEW_REMEMBER) {
                return (long) (card.getCurrentInterval() * card.getIFactor());
            }
            if (rating == RatingType.REVIEW_PARTIALLY) {
                return (long) (card.getCurrentInterval() * 0.5);
            }
            if (rating == RatingType.REVIEW_FORGOT) {
                return (long) (card.getCurrentInterval() * card.getRFactor());
            }
        }
        return null;
    }

    /**
     * Updates the state of a flashcard in Learn Mode based on the provided rating.
     * Adjusts the flashcard's progress in the learning process by either stepping
     * up or down in Learn Mode.
     *
     * @param item   The {@link Flashcard} currently in Learn Mode whose state
     *               will be updated.
     * @param rating The {@link RatingType} applied to the flashcard, which must
     *               start with "LEARN". This rating determines the update logic
     *               for the learning progress.
     * @throws IllegalStateException    if the flashcard is not in Learn Mode or if
     *                                  the learning step is {@code null}.
     * @throws IllegalArgumentException if a non-Learn Mode rating is provided.
     * @see #stepUpInLearnMode
     * @see #stepDownInLearnMode
     */
    private void applyLearnRating(Flashcard item, RatingType rating) {
        if (!item.isInLearnMode() || item.getLearnModeStep() == null) {
            throw new IllegalStateException("Learn rating couldn't be applied to a flashcard in review mode.");
        }
        if (!rating.name().startsWith("LEARN")) {
            throw new IllegalArgumentException("Review rating couldn't be applied to a flashcard in learn mode.");
        }
        switch (rating) {
            case LEARN_KNOW -> stepUpInLearnMode(item);
            case LEARN_DONT_KNOW -> stepDownInLearnMode(item);
        }
    }

    /**
     * Applies a review rating to a given flashcard during Review Mode.
     * Updates the flashcard's interval and other related properties based on the provided rating.
     *
     * @param card   The {@link Flashcard} that the review rating will be applied to. Must not be
     *               in Learn Mode, as review ratings are only valid in Review Mode.
     * @param rating The {@link RatingType} defining the user's feedback during Review Mode.
     *               Must start with "REVIEW" to be valid.
     * @throws IllegalStateException    if the flashcard is in Learn Mode.
     * @throws IllegalArgumentException if the provided rating is not a valid Review Mode rating.
     * @see #increaseIFactorByOneUnit
     * @see #decreaseIFactorByOneUnit
     * @see #increaseRFactorByOneUnit
     * @see #decreaseRFactorByOneUnit
     */
    private void applyReviewRating(Flashcard card, RatingType rating) {
        if (card.isInLearnMode()) {
            throw new IllegalStateException("Review rating couldn't be applied to a flashcard in learn mode.");
        }
        if (!rating.name().startsWith("REVIEW")) {
            throw new IllegalArgumentException("Learn rating couldn't be applied to a flashcard in review mode.");
        }
        switch (rating) {
            case REVIEW_FORGOT -> {
                card.setCurrentInterval((long) (card.getCurrentInterval() * card.getRFactor()));
                card.setNextView(dtt.getNowUtc().plusMinutes(card.getCurrentInterval()));
                decreaseIFactorByOneUnit(card);
                decreaseRFactorByOneUnit(card);
            }
            case REVIEW_PARTIALLY -> {
                card.setCurrentInterval((long) (card.getCurrentInterval() * 0.5));
                card.setNextView(dtt.getNowUtc().plusMinutes(card.getCurrentInterval()));
            }
            case REVIEW_REMEMBER -> {
                card.setCurrentInterval((long) (card.getCurrentInterval() * card.getIFactor()));
                card.setNextView(dtt.getNowUtc().plusMinutes(card.getCurrentInterval()));
                increaseIFactorByOneUnit(card);
                increaseRFactorByOneUnit(card);
            }
        }
    }

    /**
     * Advances the state of a flashcard in Learn Mode to the next learning step or switches it
     * to Review Mode if the final learning step is reached.
     * <p>
     * This method adjusts the flashcard's {@code learnModeStep}, recalculates its interval
     * based on the next step and updates the {@code nextView} timestamp accordingly.
     *
     * @param item The {@link Flashcard} in Learn Mode whose learning step is being progressed.
     *             Must have a valid {@code learnModeStep} to determine the next step or transition
     *             to Review Mode.
     * @see #switchToReviewMode
     */
    private void stepUpInLearnMode(Flashcard item) {
        if (item.getLearnModeStep() == LearnModeStep.TWO) {
            switchToReviewMode(item);
        } else {
            item.setLearnModeStep(LearnModeStep.fromNumeral(item.getLearnModeStep().getNumeral() + 1));
            long interval = Duration
                    .of(item.getLearnModeStep().getAmountToAdd(), item.getLearnModeStep().getTemporalUnit())
                    .toMinutes();
            LocalDateTime nextView = dtt.getNowUtc().plusMinutes(interval);
            item.setNextView(nextView);
            item.setCurrentInterval(interval);
        }
    }

    /**
     * Updates the state of a flashcard in Learn Mode by decrementing its learning step
     * and adjusting its interval and next review time accordingly.
     * <p>
     * This method reduces the {@code learnModeStep} of the provided flashcard
     * if it is not currently at {@link LearnModeStep#ONE}. It calculates the
     * new interval and sets the {@code nextView} and {@code currentInterval}
     * properties of the flashcard.
     *
     * @param item The {@link Flashcard} in Learn Mode whose step and related
     *             properties are being updated. Must have a valid {@code learnModeStep}.
     */
    private void stepDownInLearnMode(Flashcard item) {
        if (item.getLearnModeStep() != LearnModeStep.ONE) {
            item.setLearnModeStep(LearnModeStep.fromNumeral(item.getLearnModeStep().getNumeral() - 1));
        }
        long interval = Duration
                .of(item.getLearnModeStep().getAmountToAdd(), item.getLearnModeStep().getTemporalUnit())
                .toMinutes();
        LocalDateTime nextView = dtt.getNowUtc().plusMinutes(interval);
        item.setNextView(nextView);
        item.setCurrentInterval(interval);
    }

    /**
     * Switches a flashcard from Learn Mode to Review Mode.
     * <p>
     * This method updates the flashcard's state by disabling Learn Mode,
     * resetting the learning step to {@code null}, setting the interval
     * to the {@link #INITIAL_REVIEW_INTERVAL} and calculating the next review time.
     * It only performs the switch if the flashcard is currently in Learn Mode.
     *
     * @param item The {@link Flashcard} to switch to Review Mode.
     *             Must currently be in Learn Mode.
     * @return {@code true} if the switch to Review Mode is successful,
     * or {@code false} if the flashcard is already in Review Mode.
     *
     * @see #INITIAL_REVIEW_INTERVAL
     */
    private boolean switchToReviewMode(Flashcard item) {
        if (!item.isInLearnMode()) {
            return false;
        }
        item.setInLearnMode(false);
        item.setLearnModeStep(null);
        item.setCurrentInterval(INITIAL_REVIEW_INTERVAL);
        item.setNextView(dtt.getNowUtc().plusMinutes(item.getCurrentInterval()));
        return true;
    }

    /**
     * Increases the {@code iFactor} of the provided {@link Flashcard} by one unit, up to the defined maximum limit.
     *
     * <p>
     * The I-Factor is incremented by {@link #FACTOR_UPDATE_UNIT}. If the new value exceeds
     * {@link #I_FACTOR_UPPER_LIMIT}, the {@code iFactor} is capped at that limit.
     *
     * @param sri The {@link Flashcard} whose {@code iFactor} will be increased. The flashcard is updated in-place.
     *
     * @see #FACTOR_UPDATE_UNIT
     * @see #I_FACTOR_UPPER_LIMIT
     */
    private void increaseIFactorByOneUnit(Flashcard sri) {
        sri.setIFactor(sri.getIFactor() + FACTOR_UPDATE_UNIT);
        if (sri.getIFactor() > I_FACTOR_UPPER_LIMIT) {
            sri.setIFactor(I_FACTOR_UPPER_LIMIT);
        }
    }

    /**
     * Decreases the {@code iFactor} of the specified flashcard by one unit.
     * <p>
     * Ensures that the {@code iFactor} does not drop below the defined {@link #I_FACTOR_BOTTOM_LIMIT}.
     * If the new value exceeds {@link #I_FACTOR_BOTTOM_LIMIT}, the {@code iFactor} is capped at that limit.
     *
     * @param sri The {@link Flashcard} whose {@code iFactor} will be decreased.
     *
     * @see #FACTOR_UPDATE_UNIT
     * @see #I_FACTOR_BOTTOM_LIMIT
     */
    private void decreaseIFactorByOneUnit(Flashcard sri) {
        sri.setIFactor(sri.getIFactor() - FACTOR_UPDATE_UNIT);
        if (sri.getIFactor() < I_FACTOR_BOTTOM_LIMIT) {
            sri.setIFactor(I_FACTOR_BOTTOM_LIMIT);
        }
    }

    /**
     * Increases the {@code rFactor} of the provided {@link Flashcard} by one unit, up to the defined maximum limit.
     * <p>
     * The {@code rFactor} is incremented by {@link #FACTOR_UPDATE_UNIT}. If the new value exceeds
     * {@link #R_FACTOR_UPPER_LIMIT}, the {@code rFactor} is capped at that limit.
     *
     * @param sri The {@link Flashcard} whose {@code rFactor} will be increased. The flashcard is updated in-place.
     *
     * @see #FACTOR_UPDATE_UNIT
     * @see #R_FACTOR_UPPER_LIMIT
     */
    private void increaseRFactorByOneUnit(Flashcard sri) {
        sri.setRFactor(sri.getRFactor() + FACTOR_UPDATE_UNIT);
        if (sri.getRFactor() > R_FACTOR_UPPER_LIMIT) {
            sri.setRFactor(R_FACTOR_UPPER_LIMIT);
        }
    }

    /**
     * Decreases the {@code rFactor} of the provided {@link Flashcard} by one unit, ensuring it does not go below
     * the defined {@link #R_FACTOR_BOTTOM_LIMIT}.
     * <p>
     * The updated {@code rFactor} value is recalculated by subtracting {@link #FACTOR_UPDATE_UNIT}. If the new
     * value falls below {@link #R_FACTOR_BOTTOM_LIMIT}, it is capped at that limit.
     *
     * @param sri The {@link Flashcard} whose {@code rFactor} will be decreased. The flashcard is updated in-place.
     *
     * @see #FACTOR_UPDATE_UNIT
     * @see #R_FACTOR_BOTTOM_LIMIT
     */
    private void decreaseRFactorByOneUnit(Flashcard sri) {
        sri.setRFactor(sri.getRFactor() - FACTOR_UPDATE_UNIT);
        if (sri.getRFactor() < R_FACTOR_BOTTOM_LIMIT) {
            sri.setRFactor(R_FACTOR_BOTTOM_LIMIT);
        }
    }
}
