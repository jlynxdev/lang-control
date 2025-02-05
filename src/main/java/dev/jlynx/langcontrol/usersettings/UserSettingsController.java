package dev.jlynx.langcontrol.usersettings;

import dev.jlynx.langcontrol.usersettings.dto.UpdateUserSettingsRequest;
import dev.jlynx.langcontrol.usersettings.dto.UserSettingsOverview;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * A REST controller that provides endpoints for managing user-specific settings.
 * <p>
 * The controller communicates with {@link UserSettingsService} to retrieve and update
 * user settings.
 * <p>
 * The base endpoint is defined by the property {@code apiPref}.
 *
 * @see UserSettingsService
 * @see UserSettingsOverview
 * @see UpdateUserSettingsRequest
 */
@RequestMapping("${apiPref}/settings")
@RestController
public class UserSettingsController {

    public static final Logger LOG = LoggerFactory.getLogger(UserSettingsController.class);

    private final UserSettingsService userSettingsService;

    @Autowired
    public UserSettingsController(UserSettingsService userSettingsService) {
        this.userSettingsService = userSettingsService;
    }

    /**
     * Retrieves the current user's settings.
     *
     * <p>This method fetches user-specific configuration settings such as
     * preferences for "Dynamic Sentences" and "Zen Mode".
     *
     * @return a {@link ResponseEntity} containing the {@link UserSettingsOverview}
     * object with the user's current settings
     */
    @GetMapping
    public ResponseEntity<UserSettingsOverview> getSettings() {
        LOG.debug("getSettings() method invoked.");
        UserSettingsOverview userSettingsOverview = userSettingsService.retrieveCurrentUserSettings();
        return ResponseEntity.ok(userSettingsOverview);
    }

    /**
     * Updates the current user's settings with the provided values.
     *
     * @param body An instance of {@link UpdateUserSettingsRequest} containing the updated settings.
     * @return A {@link ResponseEntity} containing an updated {@link UserSettingsOverview} if successful.
     */
    @PutMapping
    public ResponseEntity<?> updateSettings(@RequestBody UpdateUserSettingsRequest body) {
        UserSettingsOverview overview = userSettingsService.updateCurrentUserSettings(body);
        return ResponseEntity.ok(overview);
    }
}
