package dev.jlynx.langcontrol.spacedrepetition;

import dev.jlynx.langcontrol.flashcard.Flashcard;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

/**
 * Enum representing the steps of a {@link dev.jlynx.langcontrol.flashcard.Flashcard} in a Learn Mode.
 * It is associated with a temporal unit and the amount of time to add to the flashcard's interval.
 *
 * @see BasicSpacedRepetitionAlgorithm
 * @see dev.jlynx.langcontrol.flashcard.Flashcard
 */
public enum LearnModeStep {

    /**
     * Represents the first step of a {@link Flashcard}'s Learn Mode.
     * <p>
     * This step is associated with the flashcard's interval increase of 1 minute.
     */
    ONE(1, ChronoUnit.MINUTES, 1),

    /**
     * Represents the second step of a {@link Flashcard}'s Learn Mode.
     * <p>
     * This step is associated with the flashcard's interval increase of 10 minutes.
     */
    TWO(2, ChronoUnit.MINUTES, 10);

    private final int numeral;
    private final TemporalUnit temporalUnit;
    private final int amountToAdd;

    LearnModeStep(int numeral, TemporalUnit temporalUnit, int amountToAdd) {
        this.numeral = numeral;
        this.temporalUnit = temporalUnit;
        this.amountToAdd = amountToAdd;
    }

    public int getNumeral() {
        return numeral;
    }

    /**
     * Retrieves the temporal unit associated with this {@link LearnModeStep}.
     *
     * @return the {@link TemporalUnit} corresponding to the time interval used by this {@link LearnModeStep}.
     */
    public TemporalUnit getTemporalUnit() {
        return temporalUnit;
    }

    /**
     * Retrieves the amount of time to add to the flashcard's interval based on this {@link LearnModeStep}.
     *
     * @return the amount of time to add as an integer.
     */
    public int getAmountToAdd() {
        return amountToAdd;
    }

    /**
     * Retrieves the {@link LearnModeStep} corresponding to the specified numeral.
     *
     * @param numeral an integer representing the numeral associated with a {@link LearnModeStep}
     * @return the {@link LearnModeStep} matching the given numeral, or {@code null} if no match is found
     */
    public static LearnModeStep fromNumeral(int numeral) {
        for (LearnModeStep step : LearnModeStep.values()) {
            if (step.getNumeral() == numeral) {
                return step;
            }
        }
        return null;
    }
}
