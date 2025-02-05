package dev.jlynx.langcontrol.generator.openai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration class for creating a WebClient bean to interact with the OpenAI API.
 * <p>
 * This class provides the necessary configuration such as base URL and authorisation
 * headers to facilitate communication with the OpenAI platform. It relies on a
 * configured API key, which is injected as a property value.
 * <p>
 * Components or services that require interaction with the OpenAI API can use the
 * configured WebClient via dependency injection.
 */
@Configuration
public class OpenaiClientConfig {

    private static final String baseUrl = "https://api.openai.com/v1";

    @Value("${openai.api.key}")
    private String openaiApiKey;

    /**
     * Creates a {@link WebClient} bean configured for communication with the OpenAI API.
     * The WebClient is pre-configured with a base URL pointing to the OpenAI API
     * and includes default headers for authorisation using an API key and content type set to JSON.
     *
     * @return a {@link WebClient} instance configured for OpenAI API interactions.
     */
    @Bean(name = "openaiWebClient")
    public WebClient openaiWebClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + openaiApiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();
    }
}
