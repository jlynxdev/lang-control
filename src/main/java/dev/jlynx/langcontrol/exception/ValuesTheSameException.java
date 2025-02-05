package dev.jlynx.langcontrol.exception;

/**
 * Thrown when two values that are expected to be different are found to be the same.
 */
public class ValuesTheSameException extends RuntimeException {

    public ValuesTheSameException() {
    }

    public ValuesTheSameException(String message) {
        super(message);
    }
}
