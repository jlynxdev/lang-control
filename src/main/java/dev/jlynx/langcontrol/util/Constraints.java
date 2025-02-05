package dev.jlynx.langcontrol.util;

/**
 * Defines application-wide constraints for validating user inputs such as passwords,
 * usernames and first names.
 *
 * <p>This class provides regular expression patterns and numerical boundaries for
 * validation rules. It works in conjunction with {@link ErrorMsg} to handle input validation
 * errors.
 */
public class Constraints {

    /**
     * A regular expression defining a valid password pattern.
     *
     * <p>The password must adhere to the following constraints:
     * <ul>
     *   <li>Contain at least one lowercase letter.</li>
     *   <li>Contain at least one uppercase letter.</li>
     *   <li>Contain at least one digit.</li>
     *   <li>Contain at least one special character.</li>
     *   <li>Be at least {@link Constraints#PWD_MIN} characters long.</li>
     * </ul>
     *
     * This pattern is used in conjunction with {@link ErrorMsg#PASSWORD_PATTERN}
     * for enforcing password validation rules.
     *
     * @see ErrorMsg#PASSWORD_PATTERN
     */
    public static final String PWD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\da-zA-Z]).{8,}$";

    /**
     * A regular expression pattern for validating usernames.
     *
     * <ul>
     *   <li>The username can only consist of alphanumeric characters and underscores.</li>
     *   <li>Underscores cannot occur consecutively (e.g. "__" is invalid).</li>
     * </ul>
     *
     * @see ErrorMsg#USERNAME_PATTERN
     */
    public static final String USERNAME_REGEX = "^(?!\\w*_{2,})\\w*$";

    /**
     * A regular expression defining the valid pattern for first names.
     *
     * <ul>
     *   <li>Matches only alphabetic characters (both uppercase and lowercase).</li>
     *   <li>Does not allow spaces, numbers or special characters.</li>
     * </ul>
     *
     * Used to ensure the validity of first names in compliance with the application's constraints.
     *
     * @see ErrorMsg#NAME_PATTERN
     */
    public static final String FIRSTNAME_REGEX = "^[a-zA-Z]*$";

    /**
     * Specifies the minimum required length for a password.
     *
     * <ul>
     *   <li>The value is set to {@code 8}.</li>
     *   <li>Used alongside {@link Constraints#PWD_MAX} and {@link Constraints#PWD_REGEX}
     *       to enforce password policies.</li>
     * </ul>
     *
     * ErrorMsg#PASSWORD_PATTERNPASSWORD_TOO_SHORT
     */
    public static final int PWD_MIN = 8;

    /**
     * Represents the maximum allowable length for a password.
     * <p>
     * Passwords exceeding {@code PWD_MAX} characters are considered invalid.
     *
     * @see Constraints#PWD_MIN
     * @see Constraints#PWD_REGEX
     * @see ErrorMsg#PASSWORD_TOO_LONG
     */
    public static final int PWD_MAX = 50;

    /**
     * Represents the minimum length requirement for a username.
     * A username must be at least {@value #USERNAME_MIN} characters long.
     *
     * <p>Used in conjunction with {@link Constraints#USERNAME_MAX} and
     * {@link Constraints#USERNAME_REGEX} to validate usernames.
     *
     * @see ErrorMsg#USERNAME_TOO_SHORT
     */
    public static final int USERNAME_MIN = 4;

    /**
     * Defines the maximum length for a username.
     *
     * <ul>
     *     <li>The value is set to {@value #USERNAME_MAX}, meaning usernames cannot exceed 30 characters.</li>
     *     <li>Used in conjunction with {@link #USERNAME_MIN} and {@link #USERNAME_REGEX}
     *     to enforce username constraints.</li>
     * </ul>
     *
     * @see ErrorMsg#USERNAME_TOO_LONG
     */
    public static final int USERNAME_MAX = 30;

    /**
     * Represents the maximum allowable length for a first name in characters.
     *
     * <ul>
     *   <li>The value is set to {@value #FIRSTNAME_MAX}, meaning first names cannot exceed 50 characters.</li>
     *   <li>Used alongside {@link Constraints#FIRSTNAME_REGEX} to enforce first name validity.</li>
     * </ul>
     *
     * @see ErrorMsg#NAME_TOO_LONG
     */
    public static final int FIRSTNAME_MAX = 50;
}
