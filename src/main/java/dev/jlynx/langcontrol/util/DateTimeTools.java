package dev.jlynx.langcontrol.util;

import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Provides utility methods for handling date and time operations in the application.
 */
@Service
public class DateTimeTools {

    /**
     * Retrieves the current UTC date and time.
     *
     * <p>Makes use of the system's UTC clock.</p>
     *
     * @return the current {@link LocalDateTime} in UTC
     */
    public LocalDateTime getNowUtc() {
        return LocalDateTime.now(Clock.systemUTC());
    }
}
