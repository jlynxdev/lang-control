package dev.jlynx.langcontrol.admin.dto;

import dev.jlynx.langcontrol.util.Constraints;
import dev.jlynx.langcontrol.util.ErrorMsg;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Represents a request from the admin account to update the user's password.
 *
 * <p>This record validates the current admin password and the user's new password using constraints
 * defined in {@link Constraints} and error messages provided by {@link ErrorMsg}.
 *
 * <ul>
 *   <li>{@code adminPassword} must be provided and cannot exceed the maximum length specified
 *   by {@link Constraints#PWD_MAX}.</li>
 *   <li>{@code newPassword} must satisfy multiple constraints, including:
 *       <ul>
 *         <li>Being provided.</li>
 *         <li>Having a length between {@link Constraints#PWD_MIN} and {@link Constraints#PWD_MAX}.</li>
 *         <li>Adhering to the pattern specified by {@link Constraints#PWD_REGEX}
 *         to ensure complexity requirements.</li>
 *       </ul>
 *   </li>
 * </ul>
 */
public record UpdatePasswordAdminRequest(
        @NotBlank(message = ErrorMsg.PASSWORD_NOT_PROVIDED)
        @Size(max = Constraints.PWD_MAX, message = ErrorMsg.PASSWORD_TOO_LONG)
        String adminPassword,

        @NotBlank(message = ErrorMsg.PASSWORD_NOT_PROVIDED)
        @Size(min = Constraints.PWD_MIN, message = ErrorMsg.PASSWORD_TOO_SHORT)
        @Size(max = Constraints.PWD_MAX, message = ErrorMsg.PASSWORD_TOO_LONG)
        @Pattern(regexp = Constraints.PWD_REGEX,
                message = ErrorMsg.PASSWORD_PATTERN)
        String newPassword
) {}
