package dev.jlynx.langcontrol.flashcard;

/**
 * Enum to represent sorting criteria for flashcards.
 * <p>
 * Each enum constant specifies a unique sorting criterion utilised in queries or UI sorting logic.
 * The criteria are defined by a URL parameter and the associated entity field name in the @link Flashcard class.
 * </p>
 * <ul>
 *     <li>{@code INTERVAL}: Sorts based on the flashcard's current review interval.</li>
 *     <li>{@code TARGET_WORD}: Sorts by the flashcard's target word in the learning process.</li>
 *     <li>{@code CREATED_AT}: Sorts flashcards by their creation timestamp.</li>
 * </ul>
 */
public enum FlashcardSortBy {

    INTERVAL("interval", "currentInterval"),
    TARGET_WORD("target", "targetWord"),
    CREATED_AT("created", "createdAt");

    /**
     * Represents the unique URL parameter value that corresponds to a specific
     * sorting criterion in the {@link FlashcardSortBy} enum.
     * <p>
     * This value is utilised to construct query parameters for API requests or to
     * handle sorting logic in user interfaces.
     */
    private final String urlValue;

    /**
     * Represents the name of the field in the {@link Flashcard} class used for sorting logic.
     * <p>
     * This value corresponds to a specific property within the {@link Flashcard} entity
     * and is utilised in database queries or UI sorting mechanisms.
     * </p>
     *
     * @param fieldName The name of the associated field in the {@link Flashcard} class.
     */
    private final String fieldName;

    FlashcardSortBy(String urlValue, String fieldName) {
        this.urlValue = urlValue;
        this.fieldName = fieldName;
    }

    public String getUrlValue() {
        return urlValue;
    }

    public String getFieldName() {
        return fieldName;
    }
}
