package dev.jlynx.langcontrol.deck;

import dev.jlynx.langcontrol.deck.dto.CreateDeckRequest;
import dev.jlynx.langcontrol.deck.dto.DeckDetails;
import dev.jlynx.langcontrol.deck.dto.DeckOverview;
import dev.jlynx.langcontrol.deck.dto.UpdateDeckRequest;
import dev.jlynx.langcontrol.deck.view.DeckView;
import dev.jlynx.langcontrol.exception.AccessForbiddenException;
import dev.jlynx.langcontrol.exception.AssetNotFoundException;
import dev.jlynx.langcontrol.exception.DeckCreationException;
import dev.jlynx.langcontrol.flashcard.WordFlashcardService;
import dev.jlynx.langcontrol.flashcard.dto.WordFlashcardView;
import dev.jlynx.langcontrol.userprofile.UserProfile;
import dev.jlynx.langcontrol.userprofile.UserProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class DeckServiceImpl implements DeckService {

    private static final Logger LOG = LoggerFactory.getLogger(DeckServiceImpl.class);

    private final DeckRepository deckRepository;
    private final UserProfileService userProfileService;
    private final WordFlashcardService wordFlashcardService;

    @Autowired
    public DeckServiceImpl(DeckRepository deckRepository, UserProfileService userProfileService,
                           WordFlashcardService wordFlashcardService) {
        this.deckRepository = deckRepository;
        this.userProfileService = userProfileService;
        this.wordFlashcardService = wordFlashcardService;
    }

    /**
     * Creates a new deck based on the provided request data.
     * <p>
     * Validates that the source and target languages are different before proceeding with deck creation.
     * Associates the new deck with the current user's profile and persists it to the database.
     *
     * @param body the {@link CreateDeckRequest} object containing the details of the deck to be created,
     *             including its name, source language and target language.
     * @return a {@link DeckOverview} object representing the summary of the newly created deck.
     * @throws DeckCreationException if the source and target languages are the same.
     */
    @Transactional
    @Override
    public DeckOverview createNewDeck(CreateDeckRequest body) {
        if (body.sourceLang() == body.targetLang()) {
            LOG.debug("Attempted to create a deck with name='{}' with equal language codes '{}' on both sides. Operation threw exception",
                    body.name(), body.sourceLang());
            throw new DeckCreationException("Source language and target language cannot be the same.");
        }
        UserProfile currentUserProfile = userProfileService.retrieveCurrentProfileEntity();

        Deck deckToCreate = Deck.fromRequest(body);
        deckToCreate.setId(null);
        deckToCreate.setFlashcards(new ArrayList<>());
        deckToCreate.setUserProfile(currentUserProfile);
        currentUserProfile.getDecks().add(deckToCreate);
        deckRepository.save(deckToCreate);
        LOG.debug("Deck with id={}, name='{}' persisted successfully", deckToCreate.getId(), deckToCreate.getName());
        return DeckOverview.fromEntity(deckToCreate);
    }

    /**
     * Retrieves all decks associated with the currently logged-in user's profile.
     *
     * <p>This method fetches the {@link UserProfile} of the current user and queries the
     * {@link DeckRepository} to return a list of decks belonging to the user's profile.
     *
     * @return a {@code List} of {@link DeckView} objects representing the decks of the current user.
     */
    @Override
    public List<DeckView> getAllCurrentUserProfileDecks() {
        UserProfile profile = userProfileService.retrieveCurrentProfileEntity();
        LOG.debug("Fetching all decks for UserProfile with id={}", profile.getId());
        return deckRepository.findByUserProfile(profile);
    }

    /**
     * Retrieves a deck by its unique identifier and returns its overview.
     * <p>
     * Ensures that the requesting user has permission to access the deck.
     *
     * @param deckId the unique identifier of the deck to retrieve
     * @return an instance of {@link DeckOverview} representing the deck details
     * @throws AssetNotFoundException   if the deck is not found
     * @throws AccessForbiddenException if the requesting user does not have permission to access the deck
     */
    @Override
    public DeckOverview getDeckById(long deckId) {
        Deck found = deckRepository.findById(deckId).orElseThrow(AssetNotFoundException::new);
        UserProfile profile = userProfileService.retrieveCurrentProfileEntity();
        if (!Objects.equals(found.getUserProfile().getId(), profile.getId())) {
            throw new AccessForbiddenException("You don't have permission to access deck of id=" + deckId);
        }
        return DeckOverview.fromEntity(found);
    }

    /**
     * Updates the details of an existing deck.
     *
     * <p>This method retrieves the deck by its {@code id}, verifies the current user's
     * access permissions then updates the deck information based on the request payload.
     * It will throw appropriate exceptions in cases of missing or unauthorised access.
     *
     * @param deckId the unique identifier of the deck to be updated.
     * @param body   the {@link UpdateDeckRequest} containing updated details of the deck.
     * @throws AssetNotFoundException   if the specified deck does not exist.
     * @throws AccessForbiddenException if the current user does not own the specified deck.
     */
    @Transactional
    @Override
    public void updateDeck(long deckId, UpdateDeckRequest body) {
        Deck deck = deckRepository.findById(deckId).orElseThrow(AssetNotFoundException::new);
        UserProfile profile = userProfileService.retrieveCurrentProfileEntity();
        if (!Objects.equals(deck.getUserProfile().getId(), profile.getId())) {
            throw new AccessForbiddenException("You don't have permission to access deck of id=" + deckId);
        }

        deckRepository.updateById(deckId, body.name());
    }

    /**
     * Deletes a deck by its ID after verifying ownership.
     *
     * @param deckId the unique identifier of the deck to be deleted
     * @throws AssetNotFoundException   if the deck does not exist
     * @throws AccessForbiddenException if the current user is not the owner of the deck
     */
    @Transactional
    @Override
    public void deleteDeck(long deckId) {
        Deck foundDeck = deckRepository.findById(deckId).orElseThrow(AssetNotFoundException::new);
        UserProfile profile = userProfileService.retrieveCurrentProfileEntity();
        if (!Objects.equals(foundDeck.getUserProfile().getId(), profile.getId())) {
            throw new AccessForbiddenException("You don't have permission to access deck of id=" + deckId);
        }
        deckRepository.delete(foundDeck);
    }

    /**
     * Extracts the details of a deck identified by the provided ID.
     * Validates the requesting user's permission to access the deck.
     *
     * @param deckId the unique identifier of the deck to be processed
     * @return a {@link DeckDetails} object containing the deck ID, deck name,
     * total number of flashcards in the deck and the number of flashcards ready for viewing
     * @throws AssetNotFoundException   if the deck with the specified {@code deckId} is not found
     * @throws AccessForbiddenException if the requesting user does not have permission to access the specified deck
     */
    @Override
    public DeckDetails extractDeckDetails(long deckId) {
        Deck deck = deckRepository.findById(deckId).orElseThrow(AssetNotFoundException::new);
        UserProfile profile = userProfileService.retrieveCurrentProfileEntity();
        if (!Objects.equals(deck.getUserProfile().getId(), profile.getId())) {
            throw new AccessForbiddenException("You don't have permission to access deck of id=" + deckId);
        }

        int totalCardNum = deck.getFlashcards().size();
        List<WordFlashcardView> readyForView = wordFlashcardService.fetchAllReadyForViewCardsByDeck(deckId);
        int readyForViewNum = readyForView.size();
        return new DeckDetails(deck.getId(), deck.getName(), totalCardNum, readyForViewNum);
    }
}
