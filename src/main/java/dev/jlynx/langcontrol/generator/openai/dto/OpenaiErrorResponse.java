package dev.jlynx.langcontrol.generator.openai.dto;

/**
 * Represents the error response structure returned by the OpenAI API.
 * <p>
 * The {@code OpenaiErrorResponse} is used to encapsulate details about errors
 * encountered while interacting with the OpenAI services. It contains a nested
 * {@link ErrorObject} record that provides specific information about the error.
 * </p>
 *
 * <p>
 * The structure includes:
 * <ul>
 *   <li>An {@code error} object, which provides detailed information about the nature
 *   of the error that occurred.</li>
 * </ul>
 *
 * <p>
 * The nested {@link ErrorObject} contains the following attributes:
 * <ul>
 *   <li>{@code message}: A human-readable description of what caused the error.</li>
 *   <li>{@code type}: The type or category of the error, typically indicating its high-level cause.</li>
 *   <li>{@code param}: An optional field specifying which request parameter triggered the error, if applicable.</li>
 *   <li>{@code code}: An error code that identifies the specific error encountered.</li>
 * </ul>
 *
 * <p>
 * This record is designed for applications interacting with OpenAI services, allowing
 * clients to handle API errors in a structured and informative way.
 * </p>
 */
public record OpenaiErrorResponse(ErrorObject error) {

    public record ErrorObject(String message, String type, String param, String code) {
    }
}
