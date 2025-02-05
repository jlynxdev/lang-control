package dev.jlynx.langcontrol.generator.deepl;

import com.deepl.api.*;
import dev.jlynx.langcontrol.lang.LanguageCode;
import dev.jlynx.langcontrol.generator.Translator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * A service for translating text using the DeepL API.
 * This class implements the {@link Translator} interface, providing functionality to translate
 * text between various languages using DeepL's translation capabilities.
 * It supports automatic language detection for the source language and adds regional
 * variants for certain target languages.
 */
@Service
public class DeeplTranslator implements Translator {

    private final com.deepl.api.Translator client;

    /**
     * Constructs a new DeeplTranslator instance with the specified DeepL API client.
     *
     * @param client The DeepL API client used to perform translation operations.
     */
    @Autowired
    public DeeplTranslator(com.deepl.api.Translator client) {
        this.client = client;
    }

    /**
     * Translates the given text into the specified target language using the DeepL API.
     *
     * @param textToTranslate The text to be translated.
     * @param translateTo     The target language in which the text should be translated, represented by its language code.
     * @return The translated text in the target language.
     * @throws RuntimeException if an error occurs during the translation process.
     *
     * @see LanguageCode
     */
    @Override
    public String translate(String textToTranslate, LanguageCode translateTo) {
        String translateToCode = addRegionSuffix(translateTo.getCode());
        TextResult response;
        try {
            response = client.translateText(
                    textToTranslate,
                    null,
                    translateToCode,
                    new TextTranslationOptions().setSentenceSplittingMode(SentenceSplittingMode.Off)
            );
        } catch (InterruptedException | DeepLException e) {
            throw new RuntimeException(e);
        }
        return response.getText();
    }

    /**
     * Translates the given text from a source language to a target language using DeepL API.
     *
     * @param textToTranslate The text to be translated.
     * @param translateTo     The target language in which the text should be translated.
     * @param translateFrom   The source language of the text. Can be null to auto-detect the language.
     * @return The translated text in the target language.
     * @throws RuntimeException if an error occurs during the translation process.
     *
     * @see LanguageCode
     */
    @Override
    public String translate(String textToTranslate, LanguageCode translateTo, LanguageCode translateFrom) {
        String translateToCode = addRegionSuffix(translateTo.getCode());
        TextResult response;
        try {
            response = client.translateText(
                    textToTranslate,
                    translateFrom == null ? null : translateFrom.getCode(),
                    translateToCode,
                    new TextTranslationOptions().setSentenceSplittingMode(SentenceSplittingMode.Off)
            );
        } catch (InterruptedException | DeepLException e) {
            throw new RuntimeException(e);
        }
        return response.getText();
    }

    /**
     * Adds to regional variant suffix to supported translation target language codes.
     * E.g. changes "en" to "en-GB" for British English or "pt" to "pt-PT" for European Portuguese.
     *
     * @param isoCode ISO6391 language code
     * @return An updated language code. If the language code doesn't support regional variants, it'll
     * be left unchanged.
     */
    private String addRegionSuffix(String isoCode) {
        if (isoCode.equals("en")) {
            return "en-GB";
        }
        if (isoCode.equals("pt")) {
            return "pt-PT";
        }
        return isoCode;
    }
}
