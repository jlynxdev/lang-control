package dev.jlynx.langcontrol.account.dto;

import dev.jlynx.langcontrol.util.Constraints;
import dev.jlynx.langcontrol.util.ErrorMsg;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Represents a request to update a user's password by providing the current
 * password and a new password that meets specified validation constraints.
 *
 * <ul>
 *     <li>{@code currentPassword}: The user's current password, which must
 *         be provided and meet the constraints defined in {@link Constraints}.</li>
 *     <li>{@code newPassword}: The new password to be set, which must meet
 *         length and pattern requirements as specified in {@link Constraints}.</li>
 * </ul>
 *
 * <p>Both {@code currentPassword} and {@code newPassword} are validated
 * to ensure that they are not null or blank and that {@code newPassword}
 * conforms to security and complexity requirements defined in {@link Constraints}.</p>
 */
public record UpdatePasswordRequest(
        @NotBlank(message = ErrorMsg.PASSWORD_NOT_PROVIDED)
        @Size(max = Constraints.PWD_MAX, message = ErrorMsg.PASSWORD_TOO_LONG)
        String currentPassword,

        @NotBlank(message = ErrorMsg.PASSWORD_NOT_PROVIDED)
        @Size(min = Constraints.PWD_MIN, message = ErrorMsg.PASSWORD_TOO_SHORT)
        @Size(max = Constraints.PWD_MAX, message = ErrorMsg.PASSWORD_TOO_LONG)
        @Pattern(regexp = Constraints.PWD_REGEX,
                message = ErrorMsg.PASSWORD_PATTERN)
        String newPassword
) {}
