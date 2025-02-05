package dev.jlynx.langcontrol.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.jlynx.langcontrol.exception.ErrorResponseBody;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

/**
 * Handles authentication failures in form login processes.
 *
 * <p>
 * This class implements {@link AuthenticationFailureHandler} to provide custom behaviour
 * for processing authentication-related exceptions during login attempts. It responds with
 * appropriate HTTP status codes and JSON error responses based on the exception type.
 * </p>
 *
 * <ul>
 * <li>{@link BadCredentialsException}: Returns HTTP 401 (Unauthorised) with a relevant error message.</li>
 * <li>{@link LockedException}: Returns HTTP 403 (Forbidden) with an "account locked" message.</li>
 * <li>{@link DisabledException}: Returns HTTP 403 (Forbidden) with an "account disabled" message.</li>
 * <li>Other exceptions: Returns HTTP 401 (Unauthorised) with a generic error message.</li>
 * </ul>
 * <p>
 * The response body is standardised using {@link ErrorResponseBody} with details such as status,
 * timestamp, request URI and error descriptions.
 *
 * @see SecurityConfig
 * @see ErrorResponseBody
 */
public class FormLoginAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private static final Logger LOG = LoggerFactory.getLogger(FormLoginAuthenticationFailureHandler.class);

    /**
     * Handles authentication failure scenarios by generating an appropriate JSON response
     * and setting the corresponding HTTP status code based on the type of exception.
     *
     * @param request  the {@link HttpServletRequest} object representing the client's request
     * @param response the {@link HttpServletResponse} object for sending the response back to the client
     * @param ex       the {@link AuthenticationException} thrown during the authentication process
     * @throws IOException      if an input or output error occurs while writing the response
     * @throws ServletException if a servlet-specific error occurs
     */
    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException ex) throws IOException, ServletException {
        ObjectMapper JSON = new ObjectMapper().findAndRegisterModules();

        if (ex instanceof BadCredentialsException) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            var body = new ErrorResponseBody(
                    HttpStatus.UNAUTHORIZED,
                    request.getRequestURI(),
                    ex,
                    "Invalid username or password."
            );
            response.getWriter().write(JSON.writeValueAsString(body));
            LOG.debug("Login attempt with invalid username or password");
        } else if (ex instanceof LockedException) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            var body = new ErrorResponseBody(
                    HttpStatus.FORBIDDEN,
                    request.getRequestURI(),
                    ex,
                    "Account is locked."
            );
            response.getWriter().write(JSON.writeValueAsString(body));
            LOG.debug("Login attempt to a locked account");
        } else if (ex instanceof DisabledException) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            var body = new ErrorResponseBody(
                    HttpStatus.FORBIDDEN,
                    request.getRequestURI(),
                    ex,
                    "Account is disabled."
            );
            response.getWriter().write(JSON.writeValueAsString(body));
            LOG.debug("Login attempt to a disabled account");
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            var body = new ErrorResponseBody(
                    HttpStatus.UNAUTHORIZED,
                    request.getRequestURI(),
                    ex,
                    "Login unsuccessful."
            );
            response.getWriter().write(JSON.writeValueAsString(body));
            LOG.debug("Login attempt unsuccessful");
        }

        response.setContentType("application/json");
        response.getWriter().flush();
    }
}
