package dev.jlynx.langcontrol.flashcard.dto;

import dev.jlynx.langcontrol.flashcard.PartOfSpeech;
import dev.jlynx.langcontrol.flashcard.WordFlashcard;
import dev.jlynx.langcontrol.lang.LanguageCode;

import java.time.LocalDateTime;

/**
 * Represents an immutable response object that provides an overview of a word flashcard.
 * This includes the flashcard's key details such as the target word, translation, language,
 * examples, part of speech and related deck information.
 *
 * @param id                     the unique identifier of the flashcard.
 * @param targetWord             the word being learned in the target language.
 * @param translatedWord         the translation of the target word in the source language.
 * @param targetLang             the language code of the target language (e.g. {@link LanguageCode}).
 * @param sourceLang             the language code of the source language (e.g. {@link LanguageCode}).
 * @param targetExample          an example sentence using the target word in the target language.
 * @param translatedExample      the translated version of the example sentence in the source language.
 * @param partOfSpeech           the part of speech of the target word (e.g. {@link PartOfSpeech}).
 * @param dynamicExamples        {@code true} if the examples are dynamically generated, otherwise {@code false}.
 * @param inLearnMode            {@code true} if the flashcard is currently in learning mode, otherwise {@code false}.
 * @param currentIntervalMinutes the interval in minutes until the flashcard is reviewed again.
 * @param createdAt              the timestamp representing when the flashcard was created.
 * @param nextView               the timestamp representing the next scheduled review of the flashcard.
 * @param deck                   the deck associated with the flashcard, represented as a {@link DeckOverview}.
 */
public record WordFlashcardOverviewResponse(
        long id,
        String targetWord,
        String translatedWord,
        LanguageCode targetLang,
        LanguageCode sourceLang,
        String targetExample,
        String translatedExample,
        PartOfSpeech partOfSpeech,
        boolean dynamicExamples,
        boolean inLearnMode,
        long currentIntervalMinutes,
//        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy hh:mm:ss")
        LocalDateTime createdAt,
        LocalDateTime nextView,
        DeckOverview deck
) {

    public record DeckOverview(long id, String name) {
    }

    public static WordFlashcardOverviewResponse fromEntity(WordFlashcard entity) {
        return new WordFlashcardOverviewResponse(
                entity.getId(),
                entity.getTargetWord(),
                entity.getTranslatedWord(),
                entity.getTargetLang(),
                entity.getSourceLang(),
                entity.getTargetExample(),
                entity.getTranslatedExample(),
                entity.getPartOfSpeech(),
                entity.isDynamicExamples(),
                entity.isInLearnMode(),
                entity.getCurrentInterval(),
                entity.getCreatedAt(),
                entity.getNextView(),
                new DeckOverview(entity.getDeck().getId(), entity.getDeck().getName())
        );
    }
}
