package dev.jlynx.langcontrol.userprofile.dto;

import dev.jlynx.langcontrol.userprofile.UserProfile;

/**
 * Represents a concise view of a user's profile.
 *
 * <p>Encapsulates the user's unique identifier and their first name while
 * omitting other detailed information.
 *
 * @param id        The unique identifier of the user.
 * @param firstName The first name of the user.
 * @see UserProfile
 */
public record UserProfileOverview(long id, String firstName) {

    /**
     * Converts a {@link UserProfile} entity into a {@link UserProfileOverview}.
     *
     * @param entity the {@link UserProfile} entity to convert
     * @return a {@link UserProfileOverview} containing the user's ID and first name
     */
    public static UserProfileOverview fromEntity(UserProfile entity) {
        return new UserProfileOverview(entity.getId(), entity.getFirstName());
    }
}
