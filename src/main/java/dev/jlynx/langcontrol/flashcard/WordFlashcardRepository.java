package dev.jlynx.langcontrol.flashcard;

import dev.jlynx.langcontrol.flashcard.dto.WordFlashcardView;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for managing {@link WordFlashcard} entities with extended functionality.
 * It provides CRUD operations, paging, sorting and custom query methods to interact with
 * the word flashcards in the database.
 *
 * <p>
 * This repository extends:
 * <ul>
 *   <li>{@link ListCrudRepository} for CRUD operations.</li>
 *   <li>{@link ListPagingAndSortingRepository} for paging and sorting support.</li>
 *   <li>{@link WordFlashcardRepositoryCustom} for additional custom methods.</li>
 * </ul>
 */
@Repository
public interface WordFlashcardRepository extends
        ListCrudRepository<WordFlashcard, Long>,
        ListPagingAndSortingRepository<WordFlashcard, Long>,
        WordFlashcardRepositoryCustom {

    /**
     * Retrieves a paginated list of {@link WordFlashcardView} for a specific user profile.
     *
     * @param userProfileId the ID of the user profile whose flashcard views are to be retrieved
     * @param pageable the pagination information including page number and size
     * @return a {@link Page} containing the {@link WordFlashcardView} objects for the specified user profile
     */
    @Query("select w from WordFlashcard w where w.deck.userProfile.id = :id")
    Page<WordFlashcardView> findAllFlashcardViews(@Param("id") Long userProfileId, Pageable pageable);

    /**
     * Finds a paginated list of {@link WordFlashcardView} by the ID of the associated deck.
     *
     * @param id the ID of the deck to filter the flashcards by
     * @param pageable the pagination information
     * @return a {@link Page} containing the filtered list of {@link WordFlashcardView}
     */
    Page<WordFlashcardView> findByDeck_Id(Long id, Pageable pageable);

    /**
     * Finds a list of {@link WordFlashcardView} that are ready to be reviewed in a specific deck.
     *
     * @param deckId the ID of the deck to filter flashcards by
     * @param viewsBeforeDatetime the maximum {@code nextView} date and time to filter flashcards
     * @param sort the sorting criteria for the results
     * @param limit the maximum number of results to retrieve
     * @return a list of {@link WordFlashcardView} filtered by deck and view readiness
     */
    @Query("select w from WordFlashcard w where w.deck.id = ?1 and w.nextView <= ?2")
    List<WordFlashcardView> findReadyForViewByDeck(Long deckId, LocalDateTime viewsBeforeDatetime, Sort sort, Limit limit);

    /**
     * Updates a {@link WordFlashcard} entity with the specified values based on its {@code id}.
     *
     * @param id the unique identifier of the {@link WordFlashcard} to update.
     * @param targetWord the new target word to set.
     * @param translatedWord the new translated word to set.
     * @param partOfSpeech the new {@link PartOfSpeech} to assign.
     * @param dynamicExamples determines whether dynamic examples should be enabled.
     * @param targetExample the new target-language example to set.
     * @param translatedExample the new translated-language example to set.
     * @return the number of rows updated in the database, typically 1 if the update succeeded or 0 if no entity was found.
     */
    @Transactional
    @Modifying
    @Query("""
            update WordFlashcard w set
            w.targetWord = :targetWord,
            w.translatedWord = :translatedWord,
            w.partOfSpeech = :partOfSpeech,
            w.dynamicExamples = :dynamicExamples,
            w.targetExample = :targetExample,
            w.translatedExample = :translatedExample
            where w.id = :id
            """)
    int updateById(@Param("id") Long id,
                   @Param("targetWord") String targetWord,
                   @Param("translatedWord") String translatedWord,
                   @Param("partOfSpeech") PartOfSpeech partOfSpeech,
                   @Param("dynamicExamples") boolean dynamicExamples,
                   @Param("targetExample") String targetExample,
                   @Param("translatedExample") String translatedExample);
}
