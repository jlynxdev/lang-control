package dev.jlynx.langcontrol.admin.dto;

import dev.jlynx.langcontrol.util.Constraints;
import dev.jlynx.langcontrol.util.ErrorMsg;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Represents a request to update user details.
 *
 * <p>This record encapsulates the necessary fields for modifying user information,
 * including their {@code username}, {@code firstName}, account status ({@code enabled}),
 * and locking state ({@code nonLocked}).
 *
 * <ul>
 *   <li>{@code username}: Must not be blank, must meet specific length constraints,
 *   and follow the defined regular expression pattern in {@link Constraints#USERNAME_REGEX}.</li>
 *   <li>{@code firstName}: Must not be blank, have a maximum length as per
 *   {@link Constraints#FIRSTNAME_MAX}, and comply with {@link Constraints#FIRSTNAME_REGEX}.</li>
 *   <li>{@code enabled}: Represents whether the user account is active.</li>
 *   <li>{@code nonLocked}: Indicates whether the user account is not locked.</li>
 * </ul>
 *
 * Input validation is performed using annotations to ensure data integrity and compliance
 * with application-wide constraints.
 */
public record UpdateUserRequest(

        @NotBlank(message = ErrorMsg.USERNAME_NOT_PROVIDED)
        @Size(min = Constraints.USERNAME_MIN, message = ErrorMsg.USERNAME_TOO_SHORT)
        @Size(max = Constraints.USERNAME_MAX, message = ErrorMsg.USERNAME_TOO_LONG)
        @Pattern(regexp = Constraints.USERNAME_REGEX, message = ErrorMsg.USERNAME_PATTERN)
        String username,

        @NotBlank(message = ErrorMsg.NAME_NOT_PROVIDED)
        @Size(max = Constraints.FIRSTNAME_MAX, message = ErrorMsg.NAME_TOO_LONG)
        @Pattern(regexp = Constraints.FIRSTNAME_REGEX, message = ErrorMsg.NAME_PATTERN)
        String firstName,

        boolean enabled,
        boolean nonLocked
) {}
