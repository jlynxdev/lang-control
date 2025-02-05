package dev.jlynx.langcontrol.account;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing {@link Account} entities.
 * Extends {@link ListCrudRepository} to provide CRUD operations.
 *
 * <p>Includes additional methods for checking existence and retrieving
 * accounts based on their username.
 */
@Repository
public interface AccountRepository extends ListCrudRepository<Account, Long> {

    /**
     * Retrieves an {@link Account} entity wrapped in an {@link Optional}
     * based on the provided username.
     *
     * @param username the username of the account to retrieve; must not be null.
     * @return an {@link Optional} containing the found {@link Account},
     * or an empty {@link Optional} if no account is found.
     */
    Optional<Account> findByUsername(String username);

    /**
     * Checks whether an {@link Account} exists with the specified username.
     *
     * @param username the username to check for existence (must not be {@code null} or empty)
     * @return {@code true} if an account with the given username exists, {@code false} otherwise
     */
    boolean existsByUsername(String username);
}
