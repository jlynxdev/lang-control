package dev.jlynx.langcontrol.generator.deepl;

import com.deepl.api.Translator;
import com.deepl.api.TranslatorOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Configuration class for initialising the DeepL's {@link Translator} client Spring bean.
 * <p>
 * This class uses configuration properties and provides a Bean definition for creating
 * an instance of the {@link Translator} interface to interact with the DeepL API.
 * The Translator instance is pre-configured with options, such as timeout, retry attempts,
 * and application information, ensuring consistent behaviour across the application.
 */
@Configuration
public class DeeplClientConfig {

    @Value("${deepl.api.key}")
    private String deeplApiKey;

    @Value("${app.version}")
    private String appVersion;

    /**
     * Creates and configures a Spring Bean for the DeepL {@link Translator} client.
     * This method initialises the {@link Translator} with a specified API key and options,
     * including timeout duration, retry attempts, and application metadata.
     *
     * @return an instance of {@link Translator}, pre-configured to interact with the DeepL API.
     */
    @Bean
    public Translator deeplClientTranslator() {
        TranslatorOptions options = new TranslatorOptions()
                .setTimeout(Duration.ofMillis(1000))
                .setMaxRetries(3)
                .setAppInfo("LangControl", appVersion);
        return new Translator(deeplApiKey, options);
    }
}
