package dev.jlynx.langcontrol.usersettings;

import dev.jlynx.langcontrol.usersettings.dto.UpdateUserSettingsRequest;
import dev.jlynx.langcontrol.usersettings.dto.UserSettingsOverview;

/**
 * Service interface that provides methods to manage user-specific settings.
 *
 * @see UserSettingsOverview
 * @see UpdateUserSettingsRequest
 */
public interface UserSettingsService {

    UserSettingsOverview retrieveCurrentUserSettings();

    UserSettingsOverview updateCurrentUserSettings(UpdateUserSettingsRequest body);
}
