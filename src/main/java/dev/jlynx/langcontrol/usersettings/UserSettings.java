package dev.jlynx.langcontrol.usersettings;

import dev.jlynx.langcontrol.userprofile.UserProfile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents the configurable settings related to a user in the system. The objects of
 * this entity are linked by a one-to-one mapping with {@link UserProfile}.
 * <p>
 * These settings control user-specific preferences, such as enabling or disabling features like
 * "Dynamic Sentences" and "Zen Mode". The class is mapped to a database table named
 * {@code user_settings} using JPA annotations.
 *
 * @see UserProfile
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user_settings")
public class UserSettings {

    /**
     * The unique identifier for an instance of {@link UserSettings}.
     * This value is auto-generated when a new record is created in the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Indicates whether the "Dynamic Sentences" feature is enabled by default
     * for a user. Stored in a non-nullable column in the database with the
     * name {@code default_dynamic_sentences}.
     */
    @Column(name = "default_dynamic_sentences", nullable = false)
    private boolean dynamicSentencesOnByDefault;

    /**
     * Indicates whether the "Zen Mode" feature is enabled for the user.
     * <p>
     * This setting is stored as a persistent preference in the database under the column {@code zen_mode}.
     */
    @Column(name = "zen_mode", nullable = false)
    private boolean zenModeEnabled;

    public UserSettings(Long id, boolean dynamicSentencesOnByDefault, boolean zenModeEnabled) {
        this.id = id;
        this.dynamicSentencesOnByDefault = dynamicSentencesOnByDefault;
        this.zenModeEnabled = zenModeEnabled;
    }

    /**
     * A factory method that creates a new UserSettings object with default settings. It's the preferred way of
     * instantiating the UserSettings class when creating/registering a new Account.
     *
     * @return A new UserSettings object with pre-set default values
     */
    public static UserSettings withDefaults() {
        return new UserSettings(null, false, false);
    }
}
