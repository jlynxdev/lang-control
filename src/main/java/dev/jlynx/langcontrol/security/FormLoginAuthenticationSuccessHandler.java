package dev.jlynx.langcontrol.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.jlynx.langcontrol.security.dto.LoginSuccessResponseBody;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

/**
 * Handles successful authentication events triggered after a user logs in via a form-based login.
 * Implements {@link AuthenticationSuccessHandler}.
 * <p>
 * On successful authentication, returns a JSON response with a success message
 * and sets the HTTP status to {@code 200 OK}.
 */
public class FormLoginAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger LOG = LoggerFactory.getLogger(FormLoginAuthenticationSuccessHandler.class);

    /**
     * Handles actions after the user's successful authentication.
     *
     * @param request        the {@link HttpServletRequest} representing the client's request during authentication
     * @param response       the {@link HttpServletResponse} used to send the success response
     * @param authentication the {@link Authentication} object containing authenticated user's details
     * @throws IOException      if an input or output exception occurs during the response handling
     * @throws ServletException if the request could not be handled
     */
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        ObjectMapper JSON = new ObjectMapper().findAndRegisterModules();

        var responseBody = new LoginSuccessResponseBody("Login successful");
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.getWriter().write(JSON.writeValueAsString(responseBody));
        response.getWriter().flush();
        LOG.debug("User login successful");
    }
}
