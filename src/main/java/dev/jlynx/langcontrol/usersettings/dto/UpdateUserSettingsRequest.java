package dev.jlynx.langcontrol.usersettings.dto;

import dev.jlynx.langcontrol.usersettings.UserSettings;

/**
 * Represents a request to update user-specific settings in the application.
 *
 * @param dynamicSentencesOnByDefault indicates whether the "Dynamic Sentences" feature
 *                                    should be enabled by default for the user
 * @param zenModeEnabled              indicates whether "Zen Mode" should be enabled for the user
 * @see UserSettings
 * @see UserSettingsOverview
 */
public record UpdateUserSettingsRequest(boolean dynamicSentencesOnByDefault, boolean zenModeEnabled) {
}
