package dev.jlynx.langcontrol.flashcard;

import dev.jlynx.langcontrol.deck.Deck;
import dev.jlynx.langcontrol.deck.DeckRepository;
import dev.jlynx.langcontrol.exception.AccessForbiddenException;
import dev.jlynx.langcontrol.exception.AssetNotFoundException;
import dev.jlynx.langcontrol.flashcard.dto.*;
import dev.jlynx.langcontrol.generator.Dictionary;
import dev.jlynx.langcontrol.spacedrepetition.RatingType;
import dev.jlynx.langcontrol.spacedrepetition.SpacedRepetitionAlgorithm;
import dev.jlynx.langcontrol.spacedrepetition.SpacedRepetitionSortBy;
import dev.jlynx.langcontrol.url.SortOrder;
import dev.jlynx.langcontrol.userprofile.UserProfile;
import dev.jlynx.langcontrol.userprofile.UserProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WordFlashcardServiceImpl implements WordFlashcardService {

    private static final Logger LOG = LoggerFactory.getLogger(WordFlashcardServiceImpl.class);

    private final WordFlashcardRepository wordFlashcardRepository;
    private final DeckRepository deckRepository;
    private final Dictionary dictionary;
    private final SpacedRepetitionAlgorithm algorithm;
    private final UserProfileService userProfileService;

    @Autowired
    public WordFlashcardServiceImpl(WordFlashcardRepository wordFlashcardRepository,
                                    DeckRepository deckRepository,
                                    Dictionary dictionary,
                                    SpacedRepetitionAlgorithm algorithm,
                                    UserProfileService userProfileService) {
        this.wordFlashcardRepository = wordFlashcardRepository;
        this.deckRepository = deckRepository;
        this.dictionary = dictionary;
        this.algorithm = algorithm;
        this.userProfileService = userProfileService;
    }

    /**
     * Creates a new word flashcard associated with a specific deck, ensuring the current user has access to the deck.
     * <p>
     * This method validates the ownership of the deck by the current user and establishes the necessary associations
     * between the flashcard and the provided data. Upon successful creation, the new flashcard is stored in the repository
     * and a response containing an overview of the flashcard is returned.
     *
     * @param body the request object containing the details for creating the flashcard. It includes:
     * @return an instance of {@link WordFlashcardOverviewResponse} representing the created flashcard.
     * @throws AssetNotFoundException   if the deck with the provided {@code deckId} cannot be found.
     * @throws AccessForbiddenException if the current user does not have access to the specified deck.
     */
    @Transactional
    @Override
    public WordFlashcardOverviewResponse createNewFlashcard(CreateWordFlashcardRequest body) {
        UserProfile currentProfile = userProfileService.retrieveCurrentProfileEntity();
        Deck deck = deckRepository.findById(body.deckId()).orElseThrow(AssetNotFoundException::new);
        if (!Objects.equals(deck.getUserProfile().getId(), currentProfile.getId())) {
            throw new AccessForbiddenException();
        }
        WordFlashcard flashcardToCreate = WordFlashcard.inInitialLearnMode()
                .withDeck(deck)
                .withSourceLang(deck.getSourceLang())
                .withTargetLang(deck.getTargetLang())
                .withTranslatedWord(body.translatedWord())
                .withTargetWord(body.targetWord())
                .withDynamicExamples(body.dynamicExamples())
                .withPos(body.partOfSpeech() == null ? PartOfSpeech.OTHER : body.partOfSpeech())
                .withTargetExample(body.targetExample() == null ? "" : body.targetExample())
                .withTranslatedExample(body.translatedExample() == null ? "" : body.translatedExample())
                .build();
        deck.addFlashcard(flashcardToCreate);
        wordFlashcardRepository.save(flashcardToCreate);
        LOG.debug("Created new WordFlashcard with id={}", flashcardToCreate.getId());
        return WordFlashcardOverviewResponse.fromEntity(flashcardToCreate);
    }

    /**
     * Creates a new word flashcard in "Zen Mode".
     *
     * <p>This method validates the associated deck's ownership by the current user, retrieves
     * translations for the target word and constructs a new flashcard with the provided and derived data.
     * The newly created flashcard is then saved to the repository and returned in the response.
     *
     * @param body The {@link CreateWordFlashcardZenModeRequest} containing the target word, deck ID
     *             and part of speech.
     * @return A {@link WordFlashcardOverviewResponse} containing the data of the newly created flashcard.
     * @throws AssetNotFoundException   If the requested deck is not found.
     * @throws AccessForbiddenException If the deck does not belong to the current user.
     */
    @Transactional
    @Override
    public WordFlashcardOverviewResponse createNewFlashcardZenMode(CreateWordFlashcardZenModeRequest body) {
        UserProfile currentProfile = userProfileService.retrieveCurrentProfileEntity();
        Deck retrievedDeck = deckRepository.findById(body.deckId()).orElseThrow(AssetNotFoundException::new);
        if (!Objects.equals(retrievedDeck.getUserProfile().getId(), currentProfile.getId())) {
            throw new AccessForbiddenException();
        }

        String targetSideTranslations = dictionary.getTranslationsList(
                        body.targetWord(),
                        retrievedDeck.getTargetLang(),
                        retrievedDeck.getSourceLang(),
                        body.partOfSpeech() == null ? PartOfSpeech.OTHER : body.partOfSpeech()
                ).stream()
                .distinct()
                .limit(3)
                .collect(Collectors.joining("; "));

        WordFlashcard flashcardToCreate = WordFlashcard.inInitialLearnMode()
                .withDeck(retrievedDeck)
                .withSourceLang(retrievedDeck.getSourceLang())
                .withTargetLang(retrievedDeck.getTargetLang())
                .withTranslatedWord(targetSideTranslations)
                .withTargetWord(body.targetWord())
                .withDynamicExamples(true)
                .withPos(body.partOfSpeech() == null ? PartOfSpeech.OTHER : body.partOfSpeech())
                .withTargetExample("")
                .withTranslatedExample("")
                .build();
        retrievedDeck.addFlashcard(flashcardToCreate);
        wordFlashcardRepository.save(flashcardToCreate);
        return WordFlashcardOverviewResponse.fromEntity(flashcardToCreate);
    }

    /**
     * Retrieves a {@link WordFlashcard} by its unique identifier.
     *
     * <p>This method ensures that the current user has permission to access the flashcard.
     * If the flashcard is not found or the user does not have access, appropriate exceptions
     * are thrown.
     *
     * @param id The unique identifier of the flashcard.
     * @return The {@link WordFlashcard} associated with the given id.
     * @throws AssetNotFoundException   if the flashcard is not found.
     * @throws AccessForbiddenException if the current user does not have access to the flashcard.
     */
    @Override
    public WordFlashcard getCardById(long id) {
        WordFlashcard found = wordFlashcardRepository.findById(id)
                .orElseThrow(AssetNotFoundException::new);

        UserProfile profile = userProfileService.retrieveCurrentProfileEntity();
        if (!Objects.equals(found.getDeck().getUserProfile().getId(), profile.getId())) {
            throw new AccessForbiddenException("You don't have permission to access this flashcard.");
        }
        return found;
    }

    /**
     * Retrieves an overview of a word flashcard by its ID.
     *
     * @param id the unique identifier of the word flashcard
     * @return a {@link WordFlashcardOverviewResponse} containing the overview details of the word flashcard
     */
    @Override
    public WordFlashcardOverviewResponse getCardOverviewById(long id) {
        WordFlashcard card = this.getCardById(id);
        return WordFlashcardOverviewResponse.fromEntity(card);
    }

    /**
     * Computes the interval forecasts for the specified flashcard based on its current state
     * and rating types using the {@link RatingType} and {@link IntervalForecastResponse}.
     *
     * @param cardId the unique identifier of the flashcard to calculate interval forecasts for.
     * @return an {@link IntervalForecastResponse} object containing interval values calculated
     * for each rating type.
     */
    @Override
    public IntervalForecastResponse getIntervalForecasts(long cardId) {
        WordFlashcard card = this.getCardById(cardId);

        IntervalForecastResponse forecast = new IntervalForecastResponse();
        forecast.setForLearnKnow(algorithm.calculateNextInterval(card, RatingType.LEARN_KNOW));
        forecast.setForLearnDontKnow(algorithm.calculateNextInterval(card, RatingType.LEARN_DONT_KNOW));
        forecast.setForReviewRemember(algorithm.calculateNextInterval(card, RatingType.REVIEW_REMEMBER));
        forecast.setForReviewPartially(algorithm.calculateNextInterval(card, RatingType.REVIEW_PARTIALLY));
        forecast.setForReviewForgot(algorithm.calculateNextInterval(card, RatingType.REVIEW_FORGOT));
        return forecast;
    }

    /**
     * Updates an existing flashcard identified by its ID with new word details.
     * <p>
     * This method ensures the flashcard belongs to the current user's profile before updating.
     * If the flashcard is not found or access is unauthorised, appropriate exceptions are thrown.
     *
     * @param cardId the unique identifier of the flashcard to be updated
     * @param body   an instance of {@link UpdateWordFlashcardRequest} containing updated flashcard details:
     * @throws AssetNotFoundException   if the flashcard with the specified ID does not exist
     * @throws AccessForbiddenException if the current user does not have permissions to modify the specified flashcard
     */
    @Override
    public void updateFlashcard(long cardId, UpdateWordFlashcardRequest body) {
        WordFlashcard card = wordFlashcardRepository.findById(cardId)
                .orElseThrow(AssetNotFoundException::new);

        UserProfile profile = userProfileService.retrieveCurrentProfileEntity();
        if (!Objects.equals(card.getDeck().getUserProfile().getId(), profile.getId())) {
            throw new AccessForbiddenException("You don't have permission to access card of id=" + cardId);
        }

        wordFlashcardRepository.updateById(
                cardId,
                body.targetWord(),
                body.translatedWord(),
                body.partOfSpeech() == null ? PartOfSpeech.OTHER : body.partOfSpeech(),
                body.dynamicExamples(),
                body.targetExample() == null ? "" : body.targetExample(),
                body.translatedExample() == null ? "" : body.translatedExample()
        );
    }

    /**
     * Deletes a flashcard from the repository based on its unique identifier.
     *
     * @param flashcardId the unique identifier of the flashcard to be deleted
     */
    @Transactional
    @Override
    public void deleteFlashcard(long flashcardId) {
        WordFlashcard foundCard = this.getCardById(flashcardId);
        wordFlashcardRepository.delete(foundCard);
    }

    /**
     * Retrieves all {@link WordFlashcardView} instances that are ready for view in a given deck.
     * <p>
     * This method ensures the current user has the necessary permissions to access the specified deck
     * and fetches flashcards that are ready for viewing based on the current UTC time.
     *
     * @param deckId the unique identifier of the deck from which flashcards are to be retrieved.
     * @return a list of {@link WordFlashcardView} instances representing the flashcards ready for viewing.
     * @throws AssetNotFoundException   if the deck with the specified {@code deckId} is not found.
     * @throws AccessForbiddenException if the current user does not have permission to access the deck.
     */
    @Override
    public List<WordFlashcardView> fetchAllReadyForViewCardsByDeck(long deckId) {
        Deck deck = deckRepository.findById(deckId).orElseThrow(AssetNotFoundException::new);

        UserProfile profile = userProfileService.retrieveCurrentProfileEntity();
        if (!Objects.equals(deck.getUserProfile().getId(), profile.getId())) {
            throw new AccessForbiddenException("You don't have permission to access deck of id=" + deckId);
        }

        List<WordFlashcardView> flashcards = wordFlashcardRepository.findReadyForViewByDeck(
                deckId,
                LocalDateTime.now(Clock.systemUTC()),
                Sort.unsorted(),
                Limit.unlimited()
        );
        return flashcards;
    }

    /**
     * Fetches a list of {@link WordFlashcardView} objects that are ready for review
     * for a specific deck. The list is retrieved based on the provided sorting criteria
     * and limited to a given number of results.
     *
     * <p>This method ensures the current user has access to the specified deck
     * and enforces the result set size to be at least one.
     *
     * @param deckId The unique identifier of the deck to fetch word flashcards from.
     * @param limit  The maximum number of flashcards to retrieve. Must be greater than zero.
     * @param sortBy The field by which the flashcards should be sorted.
     * @param order  The order in which the flashcards should be sorted,
     *               either {@code SortOrder.ASC} for ascending or {@code SortOrder.DESC} for descending.
     * @return A list of {@link WordFlashcardView} objects ready for review, sorted and limited as specified.
     * @throws IllegalArgumentException If the {@code limit} is less than one.
     * @throws AssetNotFoundException   If the deck with the given {@code deckId} does not exist.
     * @throws AccessForbiddenException If the current user does not have permission to access the specified deck.
     */
    @Override
    public List<WordFlashcardView> fetchReadyForView(long deckId, int limit, SpacedRepetitionSortBy sortBy, SortOrder order) {
        if (limit < 1) {
            throw new IllegalArgumentException("Result set limit cannot be less than one.");
        }

        Deck deck = deckRepository.findById(deckId).orElseThrow(AssetNotFoundException::new);
        UserProfile profile = userProfileService.retrieveCurrentProfileEntity();
        if (!Objects.equals(deck.getUserProfile().getId(), profile.getId())) {
            throw new AccessForbiddenException("You don't have permission to access deck of id=" + deckId);
        }

        Sort sort = Sort.by(order == SortOrder.ASC ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy.getFieldName());
        List<WordFlashcardView> readyForView = wordFlashcardRepository.findReadyForViewByDeck(
                deckId,
                LocalDateTime.now(Clock.systemUTC()),
                sort,
                Limit.of(limit)
        );
        return new ArrayList<>(readyForView);
    }

    /**
     * Retrieves a paginated list of flashcards, optionally filtered by a specific deck,
     * and sorted according to the specified criteria.
     * Ensures that the flashcards belong to the currently authenticated user.
     *
     * @param deckId   optional identifier of the deck for which flashcards need to be retrieved;
     *                 if empty, retrieves flashcards across all decks for the user
     * @param pageNum  the page number (zero-based) to retrieve
     * @param pageSize the number of flashcards per page
     * @param sortBy   the field by which to sort the flashcards; if {@code null}, no sorting is applied
     * @param order    the sorting order, either ascending or descending, defined by {@link SortOrder}
     * @return a {@link Page} of {@link WordFlashcardView} objects representing the retrieved flashcards
     * @throws AssetNotFoundException   if the specified deck does not exist
     * @throws AccessForbiddenException if the specified deck does not belong to the authenticated user
     */
    @Override
    public Page<WordFlashcardView> getFlashcards(Optional<Long> deckId, int pageNum, int pageSize, FlashcardSortBy sortBy, SortOrder order) {
        UserProfile profile = userProfileService.retrieveCurrentProfileEntity();

        Sort sort;
        if (order == SortOrder.ASC) {
            sort = sortBy == null ? Sort.unsorted() : Sort.by(sortBy.getFieldName()).ascending();
        } else {
            sort = sortBy == null ? Sort.unsorted() : Sort.by(sortBy.getFieldName()).descending();
        }
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);

        if (deckId.isEmpty()) {
            return wordFlashcardRepository.findAllFlashcardViews(profile.getId(), pageable);
        }

        Deck deck = deckRepository.findById(deckId.get()).orElseThrow(() -> {
            LOG.debug("Deck with id={} doesn't exist. AssetNotFoundException was thrown", deckId.get());
            return new AssetNotFoundException(String.format("The deck with id=%d doesn't exist.", deckId.get()));
        });
        if (!Objects.equals(deck.getUserProfile().getId(), profile.getId())) {
            LOG.debug("Tried to access deck data with id={} which doesn't belong to the current user", deckId.get());
            throw new AccessForbiddenException("You don't have permission to access deck of id=" + deckId.get());
        }
        return wordFlashcardRepository.findByDeck_Id(deckId.get(), pageable);
    }
}
