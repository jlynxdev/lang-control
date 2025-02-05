package dev.jlynx.langcontrol.admin.dto;

import dev.jlynx.langcontrol.account.Account;
import dev.jlynx.langcontrol.role.Role;
import dev.jlynx.langcontrol.userprofile.UserProfile;

import java.util.List;

/**
 * Represents a simplified overview of a user account.
 *
 * <p>This record provides essential details about a user, including:
 * <ul>
 *   <li>The unique {@code accountId} representing the user's {@link Account}.</li>
 *   <li>The {@code username}, uniquely identifying the account within the system.</li>
 *   <li>The user's {@code firstName} from the associated {@link UserProfile}.</li>
 *   <li>Status flags {@code enabled} and {@code nonLocked}, indicating the account's availability and lock status.</li>
 *   <li>A list of {@code roles} assigned to the account, represented as strings.</li>
 * </ul>
 *
 * <p>This class includes a constructor to initialise the {@code UserOverview}
 * using an {@link Account} object and its associated {@link UserProfile}.
 */
public record UserOverview(
        long accountId,
        String username,
        String firstName,
        boolean enabled,
        boolean nonLocked,
        List<String> roles
) {

    /**
     * Constructs a {@code UserOverview} object using data from an {@link Account}
     * and its associated {@link UserProfile}.
     *
     * @param account the {@link Account} containing user account data including
     *                {@code accountId}, {@code username}, status flags and roles.
     * @param profile the {@link UserProfile} containing the user's {@code firstName}.
     */
    public UserOverview(Account account, UserProfile profile) {
        this(
                account.getId(),
                account.getUsername(),
                profile.getFirstName(),
                account.isEnabled(),
                account.isAccountNonLocked(),
                account.getRoles().stream().map(Role::getValue).toList()
        );
    }
}
