package dev.jlynx.langcontrol.account.dto;

import dev.jlynx.langcontrol.account.Account;
import dev.jlynx.langcontrol.role.Role;

import java.util.List;

/**
 * Represents a lightweight response object containing an overview of
 * account details, primarily used for communication between the backend
 * and client layers.
 *
 * <p>This record encapsulates key account properties extracted from the
 * {@link Account} entity for display or data transmission purposes.</p>
 *
 * <ul>
 *     <li>{@code id}: A unique identifier for the account.</li>
 *     <li>{@code username}: The username associated with the account.</li>
 *     <li>{@code accountNonLocked}: Indicates whether the account is locked.</li>
 *     <li>{@code enabled}: Indicates whether the account is active and usable.</li>
 *     <li>{@code roles}: A list of roles assigned to the account, represented as strings.</li>
 * </ul>
 *
 * <p>An instance of this class can be created using the
 * {@link #fromEntity(Account)} method, which constructs the response object
 * by mapping relevant fields from an {@link Account} entity.</p>
 */
public record AccountOverviewResponse(
        long id,
        String username,
        boolean accountNonLocked,
        boolean enabled,
        List<String> roles
) {

    /**
     * Creates an {@link AccountOverviewResponse} instance from a given {@link Account} entity.
     *
     * @param entity the {@link Account} entity from which to extract data.
     * @return a new {@link AccountOverviewResponse} containing data mapped from the provided entity.
     */
    public static AccountOverviewResponse fromEntity(Account entity) {
        return new AccountOverviewResponse(
                entity.getId(),
                entity.getUsername(),
                entity.isAccountNonLocked(),
                entity.isEnabled(),
                entity.getRoles().stream().map(Role::getValue).toList()
        );
    }
}
