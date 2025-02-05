package dev.jlynx.langcontrol.generator.openai;

import dev.jlynx.langcontrol.flashcard.PartOfSpeech;
import dev.jlynx.langcontrol.generator.SentenceGenerator;
import dev.jlynx.langcontrol.generator.openai.dto.ChatCompletionRequest;
import dev.jlynx.langcontrol.generator.openai.dto.ChatCompletionRequest.ChatMessage;
import dev.jlynx.langcontrol.generator.openai.dto.ChatMessageRole;
import dev.jlynx.langcontrol.lang.LanguageCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * OpenaiSentenceGenerator is responsible for generating example sentences
 * in a specified language containing a given keyword.
 * It utilises the OpenAI API to produce language-based responses.
 * <p>
 * This class implements the SentenceGenerator interface and overrides its
 * generate method. The {@code generate} method takes in the keyword, language,
 * part of speech and the number of sentences to generate. It checks the
 * input validity, constructs a query for the OpenAI API and processes
 * the response to return a list of generated sentences.
 * <p>
 * Dependencies:
 * <ul>
 *   <li>OpenaiClient: Handles interactions with the OpenAI API.</li>
 * </ul>
 * Key Constants:
 * <ul>
 *   <li>MODEL: Specifies the OpenAI model being used.</li>
 *   <li>TEMPERATURE: Controls the randomness of the responses.</li>
 *   <li>MAX_TOKENS: Limits the maximum number of tokens in the generated responses.</li>
 * </ul>
 * Logging:
 * <ul>
 *   <li>Uses SLF4J Logging to log critical information, including API response
 *   status codes and numbers of generated sentences.</li>
 * </ul>
 * Error Handling:
 * <ul>
 *   <li>Throws IllegalArgumentException if the numberOfSentences is less than one.</li>
 * </ul>
 */
@Service
public class OpenaiSentenceGenerator implements SentenceGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(OpenaiSentenceGenerator.class);

    private static final String MODEL = "gpt-4o-mini";
    private static final double TEMPERATURE = 1.3;
    private static final int MAX_TOKENS = 40;

    private final OpenaiClient client;

    /**
     * Constructs an instance of {@link OpenaiSentenceGenerator}.
     * <p>
     * Initialises the class with the provided {@link OpenaiClient} instance,
     * enabling interaction with the OpenAI API to generate example sentences.
     *
     * @param client an instance of {@link OpenaiClient} used to communicate
     *               with the OpenAI API for generating sentences
     */
    @Autowired
    public OpenaiSentenceGenerator(OpenaiClient client) {
        this.client = client;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException when the {@code numberOfSentences} argument is less than 1
     */
    @Override
    public List<String> generate(String keyword, LanguageCode keywordLang, PartOfSpeech keywordPos,
                                 int numberOfSentences) {
        if (numberOfSentences < 1) {
            LOG.debug("Invalid argument value of '{}' for numberOfSentences", numberOfSentences);
            throw new IllegalArgumentException("The number of sentences to generate cannot be less than one.");
        }

        String lang = keywordLang.getFullLanguageName();
//        String systemMsgContent = "You're a creative and helpful assistant";
        String userMsgContent = String.format(
                "Create an example sentence in %s which contains the following %s %s: \"%s\". " +
                        "However, do not generate any translations of the sentence.",
                lang, lang, keywordPos.getStringValue(), keyword
        );
//        ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM, systemMsgContent);
        ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER, userMsgContent);
//        List<ChatMessage> messages = List.of(systemMessage, userMessage);
        List<ChatMessage> messages = List.of(userMessage);

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(MODEL)
                .messages(messages)
                .n(numberOfSentences)
                .temperature(TEMPERATURE)
                .maxCompletionTokens(MAX_TOKENS)
                .build();
        var response = client.getChatCompletion(request);
        LOG.debug("Status code {} returned. Number of generated sentences: {}",
                response.getStatusCode(), response.getBody().choices().size());
        return response.getBody().choices().stream()
                .map(choice -> choice.message().content())
                .toList();
    }
}
