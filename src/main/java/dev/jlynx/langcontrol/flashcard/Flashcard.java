package dev.jlynx.langcontrol.flashcard;

import dev.jlynx.langcontrol.spacedrepetition.BasicSpacedRepetitionAlgorithm;
import dev.jlynx.langcontrol.util.DateTimeTools;
import jakarta.persistence.*;
import dev.jlynx.langcontrol.deck.Deck;
import dev.jlynx.langcontrol.lang.LanguageCode;
import dev.jlynx.langcontrol.spacedrepetition.LearnModeStep;
import lombok.Getter;
import lombok.Setter;

import java.time.*;

/**
 * Represents a generic flashcard used in a spaced repetition system.
 * Handles fields and operations key to tracking the flashcard's review cycle,
 * including interval scheduling, language configuration and deck association.
 * <p>
 * Flashcards are customisable depending on their specific usage through inheritance.
 * </p>
 * <ul>
 *     <li>Schedules the next review session using {@code nextView}.</li>
 *     <li>Manages learning or review modes using {@code inLearnMode} and {@code learnModeStep}.</li>
 *     <li>Supports multilingual capabilities with {@code sourceLang} and {@code targetLang}.</li>
 *     <li>Associates each flashcard with a {@link Deck}.</li>
 * </ul>
 * Flashcards leverage their respective spaced repetition algorithm configuration for performance.
 */
@Getter @Setter
@Entity
@Table(name = "flashcard")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
public class Flashcard {

    /**
     * Specifies the initial interval increase factor value used by the spaced repetition algorithm.
     * <p>
     * The {@code INITIAL_IFACTOR} is set to the default value defined in
     * {@link BasicSpacedRepetitionAlgorithm#INITIAL_IFACTOR}.
     */
    @Transient
    protected static final float INITIAL_IFACTOR = BasicSpacedRepetitionAlgorithm.INITIAL_IFACTOR;

    /**
     * A constant representing the initial interval reduction factor used in the
     * {@link BasicSpacedRepetitionAlgorithm}. This value is used to initialise
     * the retention factor for spaced repetition calculations.
     */
    @Transient
    protected static final float INITIAL_RFACTOR = BasicSpacedRepetitionAlgorithm.INITIAL_RFACTOR;

    /**
     * Represents the initial interval for the first review session in spaced repetition algorithms.
     * <p>
     * This value is defined by {@link BasicSpacedRepetitionAlgorithm#INITIAL_REVIEW_INTERVAL}.
     */
    @Transient
    public static final long INITIAL_REVIEW_INTERVAL = BasicSpacedRepetitionAlgorithm.INITIAL_REVIEW_INTERVAL;

    /**
     * Represents the initial learning mode step used in the basic spaced repetition algorithm.
     * <p>
     * This constant is defined as the starting point for the learning process and
     * is initialised to the value of {@link BasicSpacedRepetitionAlgorithm#INIT_LMS}.
     */
    @Transient
    public static final LearnModeStep INIT_LMS = BasicSpacedRepetitionAlgorithm.INIT_LMS;


    /**
     * Represents the unique identifier for an entity in the database.
     * <p>
     * This field is automatically generated using the {@code GenerationType.IDENTITY} strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    /**
     * Indicates whether the entity is currently in learn mode.
     * <p>
     * This field cannot be null and is mapped to the database column {@code in_learn_mode}.
     */
    @Column(name = "in_learn_mode", nullable = false)
    protected boolean inLearnMode;

    /**
     * Represents the current step of the learning mode process.
     * <p>
     * This field maps to the database column {@code learn_mode_step}.
     *
     * @see LearnModeStep
     */
    @Column(name = "learn_mode_step")
    protected LearnModeStep learnModeStep;

    /**
     * Card's next view datetime in UTC set by the spaced repetition algorithm.
     */
    @Column(name = "next_view_utc", columnDefinition = "DATETIME")
    protected LocalDateTime nextView;

    /**
     * Currently applied interval in minutes.
     */
    @Column(name = "current_interval")
    protected Long currentInterval;

    /**
     * Represents the increase factor used for increasing the flashcard interval.
     * This field is mapped to the database column {@code increase_factor} and cannot be null.
     */
    @Column(name = "increase_factor", nullable = false)
    protected Float iFactor;

    /**
     * Represents the reduction factor of a flashcard interval.
     * This value is stored in the database column {@code reduce_factor} and cannot be null.
     * It is used to reduce the interval length of a flashcard.
     */
    @Column(name = "reduce_factor", nullable = false)
    protected Float rFactor;

    /**
     * Card's creation datetime in UTC.
     */
    @Column(name = "created_at", columnDefinition = "DATETIME", nullable = false)
    protected LocalDateTime createdAt;

    /**
     * The language that the user knows.
     */
    @Column(name = "source_lang_code", columnDefinition = "VARCHAR(2)", nullable = false)
    protected LanguageCode sourceLang;

    /**
     * The language that the user is learning.
     */
    @Column(name = "target_lang_code", columnDefinition = "VARCHAR(2)", nullable = false)
    protected LanguageCode targetLang;

    /**
     * Represents the association between a flashcard and its {@link Deck}.
     * This ensures that each flashcard is linked to a specific deck.
     *
     * <p>
     * Mapped with {@code ManyToOne} relationship, eagerly fetched and associated
     * through the foreign key constraint @code fk_flashcard_deck.
     *
     * @param deck The @link Deck entity that the flashcard belongs to.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "deck_id", nullable = false, foreignKey = @ForeignKey(name = "fk_flashcard_deck"))
    protected Deck deck;

    protected Flashcard() { }

    protected Flashcard(Deck deck, LanguageCode sourceLang, LanguageCode targetLang, boolean initialReviewMode) {
        if (initialReviewMode) {
            this.inLearnMode = false;
            this.learnModeStep = null;
            this.currentInterval = INITIAL_REVIEW_INTERVAL;
        } else {
            this.inLearnMode = true;
            this.learnModeStep = INIT_LMS;
            this.currentInterval = Duration
                    .of(learnModeStep.getAmountToAdd(), learnModeStep.getTemporalUnit())
                    .toMinutes();
        }
        this.nextView = LocalDateTime.now(Clock.systemUTC()).plusMinutes(currentInterval);

        this.id = null;
        this.iFactor = INITIAL_IFACTOR;
        this.rFactor = INITIAL_RFACTOR;
        this.createdAt = new DateTimeTools().getNowUtc();
        this.deck = deck;
        this.sourceLang = sourceLang;
        this.targetLang = targetLang;
    }

    /**
     * Updates the new current interval value and sets the {@code nextView} datetime accordingly.
     *
     * @param newInterval flashcard's new interval in minutes
     */
    public void updateNextView(long newInterval) {
        this.setCurrentInterval(newInterval);
        this.setNextView(new DateTimeTools().getNowUtc().plusMinutes(this.getCurrentInterval()));
    }
}
