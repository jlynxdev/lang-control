package dev.jlynx.langcontrol.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.Clock;
import java.time.ZonedDateTime;

/**
 * Encapsulates details pertaining to an error response sent from the server.
 * <p>
 * This class is used to standardise error responses, providing essential
 * contextual data about an error such as status, timestamp, request URI
 * and error descriptions.
 * <p>
 * Instances of {@code ErrorResponseBody} can be created using the provided constructors,
 * with the ability to initialise with or without additional error descriptions.
 */
@Getter @Setter
public class ErrorResponseBody {

    /**
     * Represents the HTTP status code of an error response.
     * <p>
     * The status signifies the type of error encountered and matches values defined by {@link HttpStatus}.
     */
    private HttpStatus status;

    /**
     * Represents the timestamp when the error occurred.
     * <p>
     * The value is formatted as a string using the pattern {@code "dd-MM-yyyy hh:mm:ss Z"}
     * and stored as a {@link ZonedDateTime}.
     * <p>
     * Used to provide temporal context for the occurrence of the error.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss Z")
    private ZonedDateTime timestamp;

    /**
     * Stores the URI of the HTTP request that caused an error.
     * <p>
     * This field is used to provide additional context about the source of the error
     * when creating an {@link ErrorResponseBody}.
     */
    private String requestURI;

    /**
     * Stores the error message providing details about the exception or issue encountered.
     */
    private String message;

    /**
     * Provides additional details about the nature of an error.
     * <p>
     * This field aims to complement the {@code message} field by offering more descriptive or contextual
     * information about the encountered problem.
     */
    private String errorDescription;

    public ErrorResponseBody(HttpStatus status, String requestURI, Throwable ex) {
        this.status = status;
        this.timestamp = ZonedDateTime.now(Clock.systemUTC());
        this.requestURI = requestURI;
        this.message = ex.getMessage();
        this.errorDescription = "";
    }

    public ErrorResponseBody(HttpStatus status, String requestURI, Throwable ex, String errorDescription) {
        this.status = status;
        this.timestamp = ZonedDateTime.now(Clock.systemUTC());
        this.requestURI = requestURI;
        this.message = ex.getMessage();
        this.errorDescription = errorDescription;
    }
}
