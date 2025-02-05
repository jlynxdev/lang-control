package dev.jlynx.langcontrol.security.dto;

/**
 * Represents the response body returned upon successful authentication within the application.
 *
 * @param authMessage a textual message indicating the success of the authentication process
 */
public record LoginSuccessResponseBody(String authMessage) {
}
