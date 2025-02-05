package dev.jlynx.langcontrol.userprofile;

import dev.jlynx.langcontrol.account.Account;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing {@link UserProfile} entities. Provides an abstraction layer
 * for CRUD operations and custom queries.
 *
 * <p>Extends {@link ListCrudRepository} for basic CRUD operation support.
 *
 * @see UserProfile
 */
@Repository
public interface UserProfileRepository extends ListCrudRepository<UserProfile, Long> {

    /**
     * Retrieves an {@link Optional} containing a {@link UserProfile} associated with the given
     * account identifier.
     *
     * @param id the ID of the associated {@link Account}.
     * @return an {@link Optional} containing the matching {@link UserProfile}, or an empty
     * {@link Optional} if no match is found.
     */
    Optional<UserProfile> findByAccount_Id(Long id);
}
