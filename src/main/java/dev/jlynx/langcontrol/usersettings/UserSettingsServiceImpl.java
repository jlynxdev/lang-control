package dev.jlynx.langcontrol.usersettings;

import dev.jlynx.langcontrol.userprofile.UserProfile;
import dev.jlynx.langcontrol.userprofile.UserProfileService;
import dev.jlynx.langcontrol.usersettings.dto.UpdateUserSettingsRequest;
import dev.jlynx.langcontrol.usersettings.dto.UserSettingsOverview;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserSettingsServiceImpl implements UserSettingsService {

    private final UserProfileService userProfileService;
    private final UserSettingsRepository userSettingsRepository;

    @Autowired
    public UserSettingsServiceImpl(UserProfileService userProfileService,
                                   UserSettingsRepository userSettingsRepository) {
        this.userProfileService = userProfileService;
        this.userSettingsRepository = userSettingsRepository;
    }

    /**
     * Retrieves the current user's settings by fetching their {@link UserProfile}
     * and extracting the associated {@link UserSettings}.
     *
     * @return an instance of {@link UserSettingsOverview} containing the current user's settings.
     */
    @Transactional
    @Override
    public UserSettingsOverview retrieveCurrentUserSettings() {
        UserProfile curProfile = userProfileService.retrieveCurrentProfileEntity();
        UserSettings curSettings = curProfile.getUserSettings();
        return UserSettingsOverview.fromEntity(curSettings);
    }

    /**
     * Updates the current user's settings based on the provided request.
     *
     * <p>This method retrieves the current user's {@link UserProfile},
     * updates the {@link UserSettings} entity with the information from
     * the {@link UpdateUserSettingsRequest}, and saves the changes to the database.
     *
     * @param body a {@link UpdateUserSettingsRequest} containing the new settings to apply
     * @return a {@link UserSettingsOverview} representing the updated settings
     */
    @Transactional
    @Override
    public UserSettingsOverview updateCurrentUserSettings(UpdateUserSettingsRequest body) {
        UserProfile curProfile = userProfileService.retrieveCurrentProfileEntity();
        UserSettings curSettings = curProfile.getUserSettings();
        curSettings.setDynamicSentencesOnByDefault(body.dynamicSentencesOnByDefault());
        curSettings.setZenModeEnabled(body.zenModeEnabled());
        userSettingsRepository.save(curSettings);
        return UserSettingsOverview.fromEntity(curSettings);
    }
}
