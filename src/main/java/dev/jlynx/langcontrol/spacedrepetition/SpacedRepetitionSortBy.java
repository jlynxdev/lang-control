package dev.jlynx.langcontrol.spacedrepetition;

import dev.jlynx.langcontrol.flashcard.Flashcard;

public enum SpacedRepetitionSortBy {

    /**
     * Represents the sorting option based on the interval of a flashcard.
     * <p>
     * This option allows sorting by the {@link Flashcard}'s {@code currentInterval}.
     */
    INTERVAL("interval", "currentInterval"),

    /**
     * Represents the sorting option based on the creation date of a flashcard.
     * <p>
     * This option allows sorting by the {@link Flashcard}'s {@code createdAt}.
     */
    CREATED_AT("created", "createdAt"),
    /**
     * Represents the sorting option for flashcards based on their next review date.
     * <p>
     * This option allows sorting by the {@link Flashcard}'s {@code nextView}.
     */
    NEXT_VIEW("view", "nextView");

    /**
     * Represents the URL-compatible string value for the sort option.
     * <p>
     * Used primarily to identify and map sorting options in url params
     * to the {@link SpacedRepetitionSortBy} enum.
     */
    private final String urlValue;

    /**
     * Represents the name of the field in the {@link Flashcard} that is used for sorting.
     */
    private final String fieldName;

    SpacedRepetitionSortBy(String urlValue, String fieldName) {
        this.urlValue = urlValue;
        this.fieldName = fieldName;
    }

    public String getUrlValue() {
        return urlValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    /**
     * Converts a URL-compatible string value into its corresponding {@link SpacedRepetitionSortBy} enum value.
     *
     * @param urlValue the URL-compatible string value to be mapped to a {@link SpacedRepetitionSortBy} constant
     * @return the {@link SpacedRepetitionSortBy} constant matching the provided {@code urlValue}, or {@code null} if no match is found
     */
    public static SpacedRepetitionSortBy fromUrlValue(String urlValue) {
        for (SpacedRepetitionSortBy sortBy : SpacedRepetitionSortBy.values()) {
            if (sortBy.getUrlValue().equals(urlValue)) {
                return sortBy;
            }
        }
        return null;
    }
}
