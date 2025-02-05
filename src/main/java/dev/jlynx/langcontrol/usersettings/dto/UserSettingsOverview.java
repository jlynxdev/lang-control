package dev.jlynx.langcontrol.usersettings.dto;

import dev.jlynx.langcontrol.usersettings.UserSettings;

/**
 * Represents a high-level overview of user settings.
 * <p>
 * This class provides a summary of key user preferences such as whether
 * "Dynamic Sentences" and "Zen Mode" features are enabled.
 *
 * @param id                          the unique identifier of the user settings
 * @param dynamicSentencesOnByDefault indicates if "Dynamic Sentences" is enabled by default
 * @param zenModeEnabled              indicates if "Zen Mode" is enabled
 * @see UserSettings
 */
public record UserSettingsOverview(long id, boolean dynamicSentencesOnByDefault, boolean zenModeEnabled) {

    /**
     * Converts an instance of {@link UserSettings} to a {@link UserSettingsOverview}.
     *
     * @param entity an instance of {@link UserSettings} containing detailed user settings data
     * @return a {@link UserSettingsOverview} representing a high-level summary of the user settings
     */
    public static UserSettingsOverview fromEntity(UserSettings entity) {
        return new UserSettingsOverview(
                entity.getId(),
                entity.isDynamicSentencesOnByDefault(),
                entity.isZenModeEnabled()
        );
    }
}
