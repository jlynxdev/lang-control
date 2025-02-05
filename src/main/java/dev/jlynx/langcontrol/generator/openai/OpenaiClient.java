package dev.jlynx.langcontrol.generator.openai;

import dev.jlynx.langcontrol.exception.OpenaiClientResponseException;
import dev.jlynx.langcontrol.generator.openai.dto.ChatCompletionRequest;
import dev.jlynx.langcontrol.generator.openai.dto.ChatCompletionResponse;
import dev.jlynx.langcontrol.generator.openai.dto.OpenaiErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * The {@code OpenaiClient} class is responsible for interacting with the OpenAI API to send requests
 * and process responses, specifically for chat-completion-related operations.
 *
 * <p>This class uses a {@link WebClient} instance to send HTTP requests and fetch responses.
 * The {@link WebClient} is configured externally and injected via the constructor.
 *
 * <p>Operations provided by this class are specific to OpenAI functionalities and handle response
 * validation, as well as error handling. Any responding error codes (4xx, 5xx) from the API will
 * result in an {@link OpenaiClientResponseException} being thrown.
 *
 * <p>Key functionalities include:
 * <ul>
 *     <li>Sending requests to OpenAI's {@code /chat/completions} endpoint.</li>
 *     <li>Processing structured request and response bodies, such as {@link ChatCompletionRequest}
 *     and {@link ChatCompletionResponse}.</li>
 *     <li>Logging interactions with the API for debugging purposes.</li>
 * </ul>
 *
 * <p>Example use cases of this client include generating AI assistant responses and managing
 * conversational AI tasks.
 *
 * <p>It is expected that this client is used within a Spring application context and declared
 * as a {@code @Component} bean, allowing it to be injected into other services as required.
 */
@Component
public class OpenaiClient {

    private static final Logger LOG = LoggerFactory.getLogger(OpenaiClient.class);

    private final WebClient client;

    /**
     * Creates an instance of {@link OpenaiClient} with a pre-configured {@link WebClient}
     * to interact with the OpenAI API.
     * <p>
     * This constructor is responsible for injecting the {@code WebClient} bean that is
     * qualified as {@code "openaiWebClient"} and configured to communicate with the
     * OpenAI API. The {@link WebClient} is typically configured with authentication
     * details, such as an API key, and a base URL for the API.
     *
     * @param client the {@link WebClient} instance qualified as {@code "openaiWebClient"},
     *               used to send requests and receive responses from the OpenAI API.
     */
    @Autowired
    public OpenaiClient(@Qualifier("openaiWebClient") WebClient client) {
        this.client = client;
    }

    /**
     * Sends a request to the OpenAI API to retrieve a chat completion based on the input parameters.
     *
     * <p>This method makes a POST request to the OpenAI API's {@code /chat/completions} endpoint
     * using the supplied request body to generate chat-based completions. If the response indicates
     * an error (e.g., a 4xx or 5xx HTTP status code), an {@link OpenaiClientResponseException} is
     * thrown after parsing the error details from the API's response.
     *
     * @param body the {@link ChatCompletionRequest} containing the input parameters, such as the model,
     *             messages, temperature, token limits, and other optional fields, to configure the
     *             completion request.
     * @return a {@link ResponseEntity} containing the {@link ChatCompletionResponse},
     * which includes generated message completions along with metadata such as usage statistics
     * and completion details.
     * @throws OpenaiClientResponseException if the OpenAI API returns an error response with a status
     *                                       code indicating failure (400-599).
     */
    public ResponseEntity<ChatCompletionResponse> getChatCompletion(ChatCompletionRequest body) {
        ResponseEntity<ChatCompletionResponse> response = client.post()
                .uri("/chat/completions")
                .bodyValue(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, resp -> {
                    OpenaiErrorResponse errorBody = resp.bodyToMono(OpenaiErrorResponse.class).block();
                    LOG.debug("OpenAI API responded with status code={}, request URI={}",
                            resp.statusCode().value(), resp.request().getURI());
                    assert errorBody != null;
                    throw new OpenaiClientResponseException(errorBody.error().message());
                })
                .toEntity(ChatCompletionResponse.class)
                .block();
        return response;
    }
}
