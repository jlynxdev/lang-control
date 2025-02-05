package dev.jlynx.langcontrol.spacedrepetition;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts {@link LearnModeStep} entities to their database column representations
 * and vice versa for persistence purposes.
 *
 * <p>
 * This converter is responsible for mapping {@code LearnModeStep} enum instances
 * to corresponding {@code Short} values for database storage and reconstructing
 * {@code LearnModeStep} instances from database values.
 *
 * <ul>
 * <li>{@link #convertToDatabaseColumn(LearnModeStep)}: Converts a {@link LearnModeStep}
 * to its {@code Short} numeral value for persistence.
 * <li>{@link #convertToEntityAttribute(Short)}: Reconstructs a {@link LearnModeStep}
 * from its {@code Short} numeral value.
 * </ul>
 * <p>
 * This converter is applied automatically with the {@code @Converter(autoApply = true)}
 * annotation.
 */
@Converter(autoApply = true)
public class LearnModeStepDatabaseConverter implements AttributeConverter<LearnModeStep, Short> {

    /**
     * Converts a {@link LearnModeStep} instance to its corresponding {@code Short} value for
     * database representation.
     *
     * @param learnModeStep the {@link LearnModeStep} instance to be converted to a short
     *                      numeral; may be {@code null}.
     * @return the short numeral value associated with the specified {@link LearnModeStep}
     * or {@code null} if the input is {@code null}.
     */
    @Override
    public Short convertToDatabaseColumn(LearnModeStep learnModeStep) {
        if (learnModeStep == null) {
            return null;
        }
        return (short) learnModeStep.getNumeral();
    }

    /**
     * Converts a {@code Short} database column value into a corresponding {@link LearnModeStep} enum instance.
     *
     * @param columnVal the {@code Short} value retrieved from the database column; may be {@code null}.
     * @return the {@link LearnModeStep} corresponding to the provided {@code Short} value
     * or {@code null} if the input is {@code null}.
     */
    @Override
    public LearnModeStep convertToEntityAttribute(Short columnVal) {
        if (columnVal == null) {
            return null;
        }
        return LearnModeStep.fromNumeral((int) columnVal);
    }
}
