package dev.jlynx.langcontrol.url;

/**
 * Enum representing the sorting order for data.
 * <ul>
 * <li>{@link #ASC} - Organises data in ascending order.</li>
 * <li>{@link #DESC} - Organises data in descending order.</li>
 * </ul>
 */
public enum SortOrder {

    /**
     * Represents an ascending sort order. Useful for organising data in an ascending sequence
     * based on the specified sorting criteria.
     * <p>
     * Use {@link #getUrlValue()} to retrieve its string representation for URL parameters.
     */
    ASC,

    /**
     * Represents the descending sorting order. Used in conjunction with sorting operations to indicate that
     * elements should be arranged in descending order.
     * <p>
     * Use {@link #getUrlValue()} to retrieve its string representation for URL parameters.
     */
    DESC;

    /**
     * Retrieves the lowercase string representation of the current enum value.
     * This is typically used for URL parameters or similar purposes.
     *
     * @return a lowercase string representation of the enum value
     */
    public String getUrlValue() {
        return this.name().toLowerCase();
    }

    /**
     * Converts a URL query parameter value into a {@link SortOrder} enum.
     *
     * @param urlValue the string value from a URL query parameter
     * @return the {@link SortOrder} corresponding to the given {@code urlValue}
     * or {@code null} if no match is found
     */
    public static SortOrder fromUrlValue(String urlValue) {
        for (SortOrder order : SortOrder.values()) {
            if (order.getUrlValue().equals(urlValue)) {
                return order;
            }
        }
        return null;
    }
}
