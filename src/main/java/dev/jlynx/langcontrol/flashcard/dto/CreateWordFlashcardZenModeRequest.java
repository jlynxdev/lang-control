package dev.jlynx.langcontrol.flashcard.dto;

import dev.jlynx.langcontrol.flashcard.PartOfSpeech;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Represents the body of a request to create a new {@link dev.jlynx.langcontrol.flashcard.WordFlashcard}
 * in "Zen Mode". Zen Mode streamlines the creation process focusing on the essential details and auto-generating
 * remaining {@code Flashcard} fields.
 *
 * @param targetWord   the word or phrase in the target language (which the user is learning); max 80 characters, required
 * @param partOfSpeech the grammatical part of speech of the {@code targetWord}; optional
 * @param deckId       the ID of the deck to which the flashcard should be added; must be greater than 0
 */
public record CreateWordFlashcardZenModeRequest(
        @NotBlank
        @Size(max = 80)
        String targetWord,

        PartOfSpeech partOfSpeech,

        @Min(1)
        long deckId
) {
}
