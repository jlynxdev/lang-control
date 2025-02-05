package dev.jlynx.langcontrol.generator.dto;

/**
 * Represents the response containing the translation result of a {@link TranslationRequest}.
 * This response is typically consumed by applications interacting with the translation service.
 *
 * @param translation the translated text in the target language specified in the request
 */
public record TranslationResponse(String translation) {
}
