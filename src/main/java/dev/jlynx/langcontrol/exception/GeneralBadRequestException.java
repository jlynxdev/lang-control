package dev.jlynx.langcontrol.exception;

/**
 * A generic 400 Bad Request exception.
 */
public class GeneralBadRequestException extends RuntimeException {

    public GeneralBadRequestException() {
    }

    public GeneralBadRequestException(String message) {
        super(message);
    }
}
