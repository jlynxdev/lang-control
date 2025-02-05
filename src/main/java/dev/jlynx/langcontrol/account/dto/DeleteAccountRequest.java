package dev.jlynx.langcontrol.account.dto;

import dev.jlynx.langcontrol.util.Constraints;
import dev.jlynx.langcontrol.util.ErrorMsg;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Represents a request to delete an account, requiring the user's password
 * to verify their identity and authorise the operation.
 *
 * <ul>
 *     <li>{@code password}: The current password of the user, which must
 *         be provided and meet the constraints defined in {@link Constraints}.</li>
 * </ul>
 *
 * <p>The {@code password} field is subject to validation constraints to
 * ensure it is not null or blank and does not exceed the maximum allowed length.</p>
 */
public record DeleteAccountRequest(
        @NotBlank(message = ErrorMsg.PASSWORD_NOT_PROVIDED)
        @Size(max = Constraints.PWD_MAX, message = ErrorMsg.PASSWORD_TOO_LONG)
        String password
) {}
