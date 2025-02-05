package dev.jlynx.langcontrol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the LangControl application.
 *
 * <p>This class configures the application as a Spring Boot application.
 * It contains the {@code main} method, which is used to launch the application.
 *
 * <p>Uses {@link SpringApplication#run} to bootstrap the application
 * using the {@code LangControlApplication} class.
 */
@SpringBootApplication
public class LangControlApplication {

    public static void main(String[] args) {
        SpringApplication.run(LangControlApplication.class, args);
    }
}
