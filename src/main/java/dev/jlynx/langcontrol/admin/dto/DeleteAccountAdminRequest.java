package dev.jlynx.langcontrol.admin.dto;

import dev.jlynx.langcontrol.util.Constraints;
import dev.jlynx.langcontrol.util.ErrorMsg;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Represents a request to delete an account by an administrator.
 *
 * <p>This record encapsulates the administrator's password, required for
 * authentication and authorisation when processing account deletion requests.
 *
 * <ul>
 *   <li>{@code adminPassword} must comply with the constraints defined in
 *   {@link Constraints#PWD_MAX} and {@link ErrorMsg#PASSWORD_NOT_PROVIDED}.</li>
 * </ul>
 *
 * Validation is enforced to ensure the password is provided and does not exceed the
 * maximum length specified.
 */
public record DeleteAccountAdminRequest(
        @NotBlank(message = ErrorMsg.PASSWORD_NOT_PROVIDED)
        @Size(max = Constraints.PWD_MAX, message = ErrorMsg.PASSWORD_TOO_LONG)
        String adminPassword
) {}
