package dev.jlynx.langcontrol.deck;

import dev.jlynx.langcontrol.deck.dto.CreateDeckRequest;
import dev.jlynx.langcontrol.flashcard.Flashcard;
import dev.jlynx.langcontrol.lang.LanguageCode;
import dev.jlynx.langcontrol.userprofile.UserProfile;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a deck of flashcards associated with a specific user and language pair.
 * Each deck has a target language (to be learned) and a source language (known by the user).
 *
 * <p>
 * A deck belongs to a {@link UserProfile} and contains a collection of {@link Flashcard}.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "deck")
public class Deck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the deck. This represents the title or identifier for a collection of flashcards.
     * <p>
     * This field is mandatory and cannot be null.
     * </p>
     */
    @Column(nullable = false)
    private String name;

    /**
     * Represents the association between a {@link Deck} and its owning {@link UserProfile}.
     *
     * <ul>
     * <li>Defines the relationship between a deck and its user profile with a foreign key constraint
     *     named {@code fk_deck_user_profile}.</li>
     * <li>Uses eager fetching to always load the associated user profile when the deck is accessed.</li>
     * </ul>
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_profile_id", foreignKey = @ForeignKey(name = "fk_deck_user_profile"))
    private UserProfile userProfile;

    /**
     * Specifies the target language code for the deck.
     * <p>
     * This represents the language that the user is learning within this {@link Deck}.
     * </p>
     * <ul>
     * <li>Mapped to the database column {@code target_lang_code}.</li>
     * <li>Uses the {@link LanguageCode} enumeration.</li>
     * </ul>
     * <p>
     * The value must be a two-character string representing an ISO 639-1 language code.
     * This field is mandatory.
     * </p>
     *
     * @see LanguageCode
     */
    @Column(name = "target_lang_code", columnDefinition = "VARCHAR(2)", nullable = false)
    private LanguageCode targetLang;

    /**
     * Represents the source language code used in the deck.
     * This denotes the language already known by the user.
     *
     * <ul>
     * <li>Mapped to the database column {@code source_lang_code}.</li>
     * <li>Stored as a two-character language code.</li>
     * <li>Cannot be {@code null}.</li>
     * </ul>
     *
     * @see LanguageCode
     */
    @Column(name = "source_lang_code", columnDefinition = "VARCHAR(2)", nullable = false)
    private LanguageCode sourceLang;

    /**
     * Represents a collection of {@link Flashcard} entities associated with this {@link Deck}.
     *
     * <ul>
     * <li>Defines a one-to-many relationship where a single deck can contain multiple flashcards.</li>
     * <li>Bidirectional relationship mapped by the {@code deck} field in the {@link Flashcard} entity.</li>
     * <li>Cascade operations ensure that changes made to the deck automatically affect its flashcards.</li>
     * <li>Uses lazy fetching to optimise performance by loading flashcards only when accessed.</li>
     * </ul>
     */
    @OneToMany(mappedBy = "deck", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Flashcard> flashcards;


    private Deck(CreateDeckRequest requestBody) {
        this.id = null;
        this.name = requestBody.name();
        this.targetLang = requestBody.targetLang();
        this.sourceLang = requestBody.sourceLang();
        this.flashcards = new ArrayList<>();
        this.userProfile = null;
    }

    /**
     * A factory method which creates a new {@link Deck} instance from the specified {@link CreateDeckRequest}.
     *
     * @param requestBody the {@link CreateDeckRequest} containing the data required to create the {@link Deck}
     * @return a new {@link Deck} instance initialised using the provided {@link CreateDeckRequest}
     */
    public static Deck fromRequest(CreateDeckRequest requestBody) {
        return new Deck(requestBody);
    }

    /**
     * Adds a {@link Flashcard} to the current {@link Deck}.
     * <p>
     * This method associates the provided {@code Flashcard} with this {@code Deck} and includes it
     * in the deck's collection of flashcards.
     * </p>
     *
     * @param flashcard the {@link Flashcard} to be added to this {@link Deck}. This must not be {@code null}.
     */
    public void addFlashcard(Flashcard flashcard) {
        flashcard.setDeck(this);
        this.flashcards.add(flashcard);
    }

    /**
     * Removes a flashcard from the deck and disassociates it from the deck.
     * <p>
     * This method updates the flashcard's {@code deck} property to {@code null} and
     * removes the flashcard from the deck's internal list of flashcards.
     * </p>
     *
     * @param flashcard The {@link Flashcard} to be removed from the deck.
     */
    public void removeFlashcard(Flashcard flashcard) {
        flashcard.setDeck(null);
        this.flashcards.remove(flashcard);
    }
}
