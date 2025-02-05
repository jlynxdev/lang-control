package dev.jlynx.langcontrol.util;

/**
 * Provides user-friendly error messages for input validation.
 *
 * <p>This class defines constant strings for common validation errors encountered during
 * user input validation for usernames, passwords and names. It is intended to be used
 * alongside {@link Constraints} to enforce validation rules and deliver corresponding
 * feedback.
 *
 * <ul>
 *   <li>Messages for usernames cover scenarios such as missing input, length constraints
 *       and pattern violations.</li>
 *   <li>Messages for passwords include checks for missing input, length constraints
 *       and complexity requirements.</li>
 *   <li>Messages for names ensure adherence to character restrictions and length limits.</li>
 * </ul>
 *
 * All messages are predefined and immutable for consistent error reporting across the application.
 */
public class ErrorMsg {

    public static final String USERNAME_NOT_PROVIDED = "Username has not been provided.";
    public static final String USERNAME_TOO_SHORT = "Username should be at least 4 characters long.";
    public static final String USERNAME_TOO_LONG = "Username cannot be longer than 30 characters.";
    public static final String USERNAME_PATTERN = "Username may only consist of letters, digits and underscores but not two underscores next to each other.";

    public static final String PASSWORD_NOT_PROVIDED = "Password has not been provided.";
    public static final String PASSWORD_TOO_SHORT = "Password must be minimum 8 characters long.";
    public static final String PASSWORD_TOO_LONG = "Password length has exceeded the maximum limit of 50 characters.";
    public static final String PASSWORD_PATTERN = "Password should be at least 8 characters long and contain at least one lowercase letter, uppercase letter, digit and special symbol.";

    public static final String NAME_NOT_PROVIDED = "Name has not been provided.";
    public static final String NAME_TOO_LONG = "Name cannot be longer than 50 characters.";
    public static final String NAME_PATTERN = "Name may only contain letters.";
}
