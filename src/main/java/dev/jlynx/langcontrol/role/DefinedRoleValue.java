package dev.jlynx.langcontrol.role;

/**
 * Represents predefined role values to be used for assigning roles to users.
 *
 * <p>
 * This enum defines constant role values, where each role is associated with its
 * corresponding string representation.
 * </p>
 *
 * <ul>
 * <li>{@code USER} - Associated with the value "ROLE_USER".</li>
 * <li>{@code ADMIN} - Associated with the value "ROLE_ADMIN".</li>
 * </ul>
 *
 * These values can be retrieved using the {@link #getValue()} method.
 */
public enum DefinedRoleValue {

    /**
     * Represents the user role with the string value {@code "ROLE_USER"}.
     *
     * <p>
     * This role is used to grant standard user-level permissions within the system.
     * The associated string value can be retrieved using {@link #getValue()}.
     * </p>
     */
    USER("ROLE_USER"),

    /**
     * Represents the admin role with the string value {@code "ROLE_ADMIN"}.
     *
     * <p>
     * This role is used to grant administrator-level permissions within the system.
     * The associated string value can be retrieved using {@link #getValue()}.
     * </p>
     *
     * @see dev.jlynx.langcontrol.admin.AdminController
     */
    ADMIN("ROLE_ADMIN");

    private final String value;

    DefinedRoleValue(String value) {
        this.value = value;
    }

    /**
     * Retrieves the string value associated with this {@link DefinedRoleValue}.
     *
     * @return the string representation of the defined role value.
     */
    public String getValue() {
        return value;
    }
}
