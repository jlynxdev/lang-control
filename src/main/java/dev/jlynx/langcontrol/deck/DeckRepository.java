package dev.jlynx.langcontrol.deck;

import dev.jlynx.langcontrol.deck.view.DeckView;
import dev.jlynx.langcontrol.userprofile.UserProfile;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link Deck} entities. Provides methods for
 * retrieving and modifying deck data in the underlying data source.
 *
 * <p>Extends {@link ListCrudRepository} to inherit basic CRUD operations.</p>
 */
@Repository
public interface DeckRepository extends ListCrudRepository<Deck, Long> {

    /**
     * Retrieves a list of {@link DeckView} objects associated with the specified {@link UserProfile}.
     *
     * @param userProfile the {@link UserProfile} for which the associated {@link DeckView} objects are to be found
     * @return a list of {@link DeckView} objects associated with the given {@link UserProfile}
     */
    List<DeckView> findByUserProfile(UserProfile userProfile);

    /**
     * Finds and returns an {@link Optional} containing a {@link Deck} whose name matches the provided value.
     * If no deck is found, an empty {@link Optional} is returned.
     *
     * @param name the name of the deck to search for. Must not be {@code null}.
     * @return an {@link Optional} containing the matching {@link Deck}, or an empty {@link Optional} if no match is found.
     */
    Optional<Deck> findByName(String name);

    /**
     * Updates the name of a {@link Deck} by its unique identifier.
     *
     * @param id the unique identifier of the {@link Deck} to be updated; must not be null
     * @param name the new name to set for the {@link Deck}; must not be null
     * @return the number of rows affected by the update operation
     */
    @Transactional
    @Modifying
    @Query("update Deck d set d.name = :name where d.id = :id")
    int updateById(@Param("id") Long id, @Param("name") String name);
}
