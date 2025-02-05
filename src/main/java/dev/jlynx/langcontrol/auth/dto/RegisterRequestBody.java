package dev.jlynx.langcontrol.auth.dto;

import dev.jlynx.langcontrol.util.Constraints;
import dev.jlynx.langcontrol.util.ErrorMsg;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


/**
 * Represents the body of a registration request containing the user's
 * {@code username}, {@code password} and {@code firstName}.
 *
 * <p>Each field is validated via annotations to ensure compliance with specific
 * constraints defined in {@link Constraints} and error messages provided by {@link ErrorMsg}.
 *
 * <ul>
 *   <li><strong>Username:</strong>
 *   <ul>
 *     <li>Cannot be {@code null} or blank.</li>
 *     <li>Must be between {@link Constraints#USERNAME_MIN} and {@link Constraints#USERNAME_MAX} characters.</li>
 *     <li>Must adhere to the regex defined in {@link Constraints#USERNAME_REGEX}.</li>
 *   </ul>
 *   </li>
 *   <li><strong>Password:</strong>
 *   <ul>
 *     <li>Cannot be {@code null} or blank.</li>
 *     <li>Must be between {@link Constraints#PWD_MIN} and {@link Constraints#PWD_MAX} characters.</li>
 *     <li>Must adhere to the complexity rules defined in {@link Constraints#PWD_REGEX}.</li>
 *   </ul>
 *   </li>
 *   <li><strong>First Name:</strong>
 *   <ul>
 *     <li>Cannot be {@code null} or blank.</li>
 *     <li>Cannot exceed {@link Constraints#FIRSTNAME_MAX} characters.</li>
 *     <li>Must follow the regex pattern defined in {@link Constraints#FIRSTNAME_REGEX}.</li>
 *   </ul>
 *   </li>
 * </ul>
 */
public record RegisterRequestBody(
        @NotNull(message = ErrorMsg.USERNAME_NOT_PROVIDED)
        @NotBlank(message = ErrorMsg.USERNAME_NOT_PROVIDED)
        @Size(min = Constraints.USERNAME_MIN, message = ErrorMsg.USERNAME_TOO_SHORT)
        @Size(max = Constraints.USERNAME_MAX, message = ErrorMsg.USERNAME_TOO_LONG)
        @Pattern(regexp = Constraints.USERNAME_REGEX, message = ErrorMsg.USERNAME_PATTERN)
        String username,

        @NotNull(message = ErrorMsg.PASSWORD_NOT_PROVIDED)
        @NotBlank(message = ErrorMsg.PASSWORD_NOT_PROVIDED)
        @Size(min = Constraints.PWD_MIN, message = ErrorMsg.PASSWORD_TOO_SHORT)
        @Size(max = Constraints.PWD_MAX, message = ErrorMsg.PASSWORD_TOO_LONG)
        @Pattern(regexp = Constraints.PWD_REGEX, message = ErrorMsg.PASSWORD_PATTERN)
        String password,

        @NotNull(message = ErrorMsg.NAME_NOT_PROVIDED)
        @NotBlank(message = ErrorMsg.NAME_NOT_PROVIDED)
        @Size(max = Constraints.FIRSTNAME_MAX, message = ErrorMsg.NAME_TOO_LONG)
        @Pattern(regexp = Constraints.FIRSTNAME_REGEX, message = ErrorMsg.NAME_PATTERN)
        String firstName
) {}
