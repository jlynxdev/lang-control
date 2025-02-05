package dev.jlynx.langcontrol.generator;

import dev.jlynx.langcontrol.lang.LanguageCode;

/**
 * Defines methods for text translation between various languages.
 * It serves as the base contract for implementing classes to provide specific translation mechanisms.
 */
public interface Translator {

    /**
     * Translates the given text into the specified target language.
     *
     * @param textToTranslate the text to be translated
     * @param translateTo     the language to translate the text into
     * @return the translated text as a string
     */
    default String translate(String textToTranslate, LanguageCode translateTo) {
        throw new UnsupportedOperationException("No interface method implementation was found.");
    }

    /**
     * Translates the given text from the specified source language to the specified target language.
     *
     * @param textToTranslate the text to be translated
     * @param translateTo     the target language for the translation
     * @param translateFrom   the source language of the text to be translated
     * @return the translated text as a string
     */
    default String translate(String textToTranslate, LanguageCode translateTo, LanguageCode translateFrom) {
        throw new UnsupportedOperationException("No interface method implementation was found.");
    }
}
